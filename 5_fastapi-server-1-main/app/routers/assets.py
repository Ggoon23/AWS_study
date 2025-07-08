from fastapi import APIRouter, Depends, HTTPException, status, UploadFile, File, Form
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select
from app.database import get_db
from app.models.asset import Asset, Tag
from app.models.folder import Folder
from app.schemas.asset import AssetCreate, AssetResponse, TagCreate
from app.dependencies import get_current_user
from app.models.user import User
from app.utils.s3 import upload_file, delete_file, generate_presigned_url
from app.utils.dynamodb import put_asset_metadata, delete_asset_metadata, get_asset_metadata
from typing import List, Optional
import json
import logging

# 로거 설정
logger = logging.getLogger(__name__)

router = APIRouter()

@router.post("", response_model=AssetResponse)
async def upload_asset(
    file: UploadFile = File(...),
    name: str = Form(...),
    description: Optional[str] = Form(None),
    folder_id: Optional[str] = Form(None),
    tags: str = Form("[]"),  # JSON string of tag names
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db)
):
    # 폴더 확인
    if folder_id:
        result = await db.execute(
            select(Folder).where(
                Folder.id == folder_id,
                Folder.user_id == current_user.id
            )
        )
        if not result.scalar_one_or_none():
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Folder not found"
            )

    # 파일 업로드
    s3_key, file_size = await upload_file(file)
    if not s3_key:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Failed to upload file"
        )

    # 태그 처리
    tag_names = json.loads(tags)
    tag_objects = []
    for tag_name in tag_names:
        # 기존 태그 찾기 또는 새로 생성
        result = await db.execute(
            select(Tag).where(
                Tag.name == tag_name,
                Tag.user_id == current_user.id
            )
        )
        tag = result.scalar_one_or_none()
        if not tag:
            tag = Tag(name=tag_name, user_id=current_user.id)
            db.add(tag)
            await db.flush()  # ID를 얻기 위해 flush
        tag_objects.append(tag)

    # 자산 생성
    asset = Asset(
        name=name,
        description=description,
        mime_type=file.content_type,
        size=file_size,
        s3_key=s3_key,
        folder_id=folder_id,
        user_id=current_user.id,
        tags=tag_objects
    )
    db.add(asset)
    await db.commit()
    await db.refresh(asset)

    # DynamoDB에 메타데이터 저장
    tag_data = [{"id": tag.id, "name": tag.name} for tag in tag_objects]
    try:
        dynamodb_success = await put_asset_metadata(
            user_id=str(current_user.id),
            asset_id=str(asset.id),
            name=name,
            description=description,
            mime_type=file.content_type or "application/octet-stream",
            size=file_size,
            s3_key=s3_key,
            folder_id=str(folder_id) if folder_id else None,
            tags=tag_data
        )
        
        if not dynamodb_success:
            logger.error(f"DynamoDB 저장 실패 - asset_id: {asset.id}, user_id: {current_user.id}")
        else:
            logger.info(f"DynamoDB 저장 성공 - asset_id: {asset.id}, user_id: {current_user.id}")
            
    except Exception as e:
        logger.error(f"DynamoDB 저장 중 예외 발생 - asset_id: {asset.id}, error: {str(e)}")

    # 태그 정보 명시적으로 가져오기
    result = await db.execute(
        select(Tag).where(Tag.id.in_([tag.id for tag in tag_objects]))
    )
    tags = result.scalars().all()

    # 응답 생성
    return AssetResponse(
        id=asset.id,
        name=asset.name,
        description=asset.description,
        folder_id=asset.folder_id,
        mime_type=asset.mime_type,
        size=asset.size,
        file_url=generate_presigned_url(asset.s3_key),
        created_at=asset.created_at,
        updated_at=asset.updated_at,
        tags=[{"id": tag.id, "name": tag.name, "created_at": tag.created_at} for tag in tags]
    )

@router.get("", response_model=List[AssetResponse])
async def get_assets(
    folder_id: Optional[int] = None,
    tag: Optional[str] = None,
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db)
):
    # 기본 쿼리 생성
    query = select(Asset).where(Asset.user_id == current_user.id)
    
    # 필터 적용
    if folder_id:
        query = query.where(Asset.folder_id == folder_id)
    
    if tag:
        query = query.join(Asset.tags).where(Tag.name == tag)
    
    # 자산 목록 조회
    result = await db.execute(query)
    assets = result.scalars().all()

    # 모든 자산의 태그 정보를 한 번에 가져오기
    asset_ids = [asset.id for asset in assets]
    if asset_ids:
        tag_result = await db.execute(
            select(Tag, Asset)
            .join(Asset.tags)
            .where(Asset.id.in_(asset_ids))
        )
        # 자산 ID별로 태그 정보 매핑
        asset_tags = {}
        for tag, asset in tag_result:
            if asset.id not in asset_tags:
                asset_tags[asset.id] = []
            asset_tags[asset.id].append({
                "id": tag.id,
                "name": tag.name,
                "created_at": tag.created_at
            })
    else:
        asset_tags = {}

    # 응답 생성
    return [
        AssetResponse(
            id=asset.id,
            name=asset.name,
            description=asset.description,
            folder_id=asset.folder_id,
            mime_type=asset.mime_type,
            size=asset.size,
            file_url=generate_presigned_url(asset.s3_key),
            created_at=asset.created_at,
            updated_at=asset.updated_at,
            tags=asset_tags.get(asset.id, [])
        )
        for asset in assets
    ]

@router.delete("/{asset_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_asset(
    asset_id: str,
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db)
):
    result = await db.execute(
        select(Asset).where(
            Asset.id == asset_id,
            Asset.user_id == current_user.id
        )
    )
    asset = result.scalar_one_or_none()
    
    if not asset:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Asset not found"
        )
    
    # S3에서 파일 삭제
    if not delete_file(asset.s3_key):
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Failed to delete file from S3"
        )
    
    # DynamoDB에서 메타데이터 삭제
    try:
        dynamodb_success = await delete_asset_metadata(str(current_user.id), str(asset_id))
        if not dynamodb_success:
            logger.error(f"DynamoDB 삭제 실패 - asset_id: {asset_id}, user_id: {current_user.id}")
        else:
            logger.info(f"DynamoDB 삭제 성공 - asset_id: {asset_id}, user_id: {current_user.id}")
    except Exception as e:
        logger.error(f"DynamoDB 삭제 중 예외 발생 - asset_id: {asset_id}, error: {str(e)}")
    
    # 데이터베이스에서 자산 삭제
    await db.delete(asset)
    await db.commit() 