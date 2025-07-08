from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select
from app.database import get_db
from app.models.folder import Folder
from app.schemas.folder import FolderCreate, FolderResponse, FolderTreeResponse
from app.dependencies import get_current_user
from app.models.user import User
from typing import List

router = APIRouter()

@router.post("", response_model=FolderResponse, status_code=status.HTTP_201_CREATED)
async def create_folder(
    folder: FolderCreate,
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db)
):
    # 부모 폴더 존재 여부 확인
    if folder.parent_id:
        result = await db.execute(
            select(Folder).where(
                Folder.id == folder.parent_id,
                Folder.user_id == current_user.id
            )
        )
        if not result.scalar_one_or_none():
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Parent folder not found"
            )

    # 새 폴더 생성
    db_folder = Folder(
        name=folder.name,
        parent_id=folder.parent_id,
        user_id=current_user.id
    )
    db.add(db_folder)
    await db.commit()
    await db.refresh(db_folder)
    
    return db_folder

@router.get("", response_model=List[FolderResponse])
async def get_folders(
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db)
):
    result = await db.execute(
        select(Folder).where(Folder.user_id == current_user.id)
    )
    return result.scalars().all()

@router.get("/tree", response_model=List[FolderTreeResponse])
async def get_folder_tree(
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db)
):
    # 루트 폴더들 가져오기
    result = await db.execute(
        select(Folder).where(
            Folder.user_id == current_user.id,
            Folder.parent_id == None
        )
    )
    root_folders = result.scalars().all()
    
    # 전체 폴더 트리 구성
    async def build_tree(folder):
        result = await db.execute(
            select(Folder).where(
                Folder.parent_id == folder.id,
                Folder.user_id == current_user.id
            )
        )
        children = result.scalars().all()
        folder_dict = FolderTreeResponse.from_orm(folder)
        folder_dict.children = [await build_tree(child) for child in children]
        return folder_dict

    return [await build_tree(folder) for folder in root_folders]

@router.delete("/{folder_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_folder(
    folder_id: str,
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db)
):
    result = await db.execute(
        select(Folder).where(
            Folder.id == folder_id,
            Folder.user_id == current_user.id
        )
    )
    folder = result.scalar_one_or_none()
    
    if not folder:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Folder not found"
        )
    
    await db.delete(folder)
    await db.commit() 