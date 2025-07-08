import boto3
from botocore.exceptions import ClientError
from app.config import settings
from typing import Dict, Any, Optional, List
from datetime import datetime
import json
import logging
import asyncio

# 로거 설정
logger = logging.getLogger(__name__)
logger.setLevel(logging.INFO)

def get_dynamodb_client():
    """DynamoDB 클라이언트 생성"""
    return boto3.client(
        'dynamodb',
        aws_access_key_id=settings.AWS_ACCESS_KEY_ID,
        aws_secret_access_key=settings.AWS_SECRET_ACCESS_KEY,
        region_name=settings.AWS_REGION
    )

async def put_asset_metadata(
    user_id: str,
    asset_id: str,
    name: str,
    description: Optional[str],
    mime_type: str,
    size: int,
    s3_key: str,
    folder_id: Optional[str],
    tags: List[Dict[str, Any]]
) -> bool:
    """자산 메타데이터를 DynamoDB에 저장"""
    try:
        # 비동기 처리를 위해 executor 사용
        loop = asyncio.get_event_loop()
        return await loop.run_in_executor(None, _put_asset_metadata_sync, 
                                        user_id, asset_id, name, description, 
                                        mime_type, size, s3_key, folder_id, tags)
    except Exception as e:
        logger.error(f"DynamoDB 비동기 저장 중 오류 발생: {str(e)}")
        return False

def _put_asset_metadata_sync(
    user_id: str,
    asset_id: str,
    name: str,
    description: Optional[str],
    mime_type: str,
    size: int,
    s3_key: str,
    folder_id: Optional[str],
    tags: List[Dict[str, Any]]
) -> bool:
    """자산 메타데이터를 DynamoDB에 저장 (동기 버전)"""
    try:
        dynamodb = get_dynamodb_client()
        created_at = datetime.utcnow().isoformat()

        item = {
            'user_id': {'S': str(user_id)},
            'asset_id': {'S': str(asset_id)},
            'name': {'S': name},
            'mime_type': {'S': mime_type},
            'size': {'N': str(size)},
            's3_key': {'S': s3_key},
            'created_at': {'S': created_at}
        }

        if description:
            item['description'] = {'S': description}
        if folder_id:
            item['folder_id'] = {'S': str(folder_id)}
        if tags:
            item['tags'] = {'L': [{'M': {
                'id': {'S': str(tag['id'])},
                'name': {'S': tag['name']}
            }} for tag in tags]}

        # 저장할 데이터 로깅
        logger.info(f"DynamoDB에 저장할 데이터: {json.dumps(item, indent=2, ensure_ascii=False)}")

        # 테이블 존재 확인
        try:
            dynamodb.describe_table(TableName=settings.DYNAMODB_TABLE_NAME)
        except ClientError as e:
            if e.response['Error']['Code'] == 'ResourceNotFoundException':
                logger.error(f"DynamoDB 테이블 '{settings.DYNAMODB_TABLE_NAME}'이 존재하지 않습니다.")
                return False
            raise

        # 데이터 저장
        response = dynamodb.put_item(
            TableName=settings.DYNAMODB_TABLE_NAME,
            Item=item,
            ReturnConsumedCapacity='TOTAL'
        )

        # 저장 성공 로깅
        logger.info(f"DynamoDB 데이터 저장 성공 - 테이블: {settings.DYNAMODB_TABLE_NAME}, user_id: {user_id}, asset_id: {asset_id}")
        logger.info(f"DynamoDB 응답: {response}")

        # 저장된 데이터 확인
        saved_item = _get_asset_metadata_sync(user_id, asset_id)
        if saved_item:
            logger.info(f"DynamoDB에서 확인된 저장 데이터: {json.dumps(saved_item, indent=2, ensure_ascii=False)}")
        else:
            logger.warning(f"DynamoDB 데이터 저장 확인 실패 - user_id: {user_id}, asset_id: {asset_id}")

        return True
    except ClientError as e:
        logger.error(f"DynamoDB 저장 중 ClientError 발생: {str(e)}")
        logger.error(f"Error Code: {e.response['Error']['Code']}")
        logger.error(f"Error Message: {e.response['Error']['Message']}")
        return False
    except Exception as e:
        logger.error(f"DynamoDB 저장 중 예외 발생: {str(e)}")
        return False

async def delete_asset_metadata(user_id: str, asset_id: str) -> bool:
    """자산 메타데이터를 DynamoDB에서 삭제"""
    try:
        loop = asyncio.get_event_loop()
        return await loop.run_in_executor(None, _delete_asset_metadata_sync, user_id, asset_id)
    except Exception as e:
        logger.error(f"DynamoDB 비동기 삭제 중 오류 발생: {str(e)}")
        return False

def _delete_asset_metadata_sync(user_id: str, asset_id: str) -> bool:
    """자산 메타데이터를 DynamoDB에서 삭제 (동기 버전)"""
    try:
        dynamodb = get_dynamodb_client()
        response = dynamodb.delete_item(
            TableName=settings.DYNAMODB_TABLE_NAME,
            Key={
                'user_id': {'S': str(user_id)},
                'asset_id': {'S': str(asset_id)}
            },
            ReturnConsumedCapacity='TOTAL'
        )
        logger.info(f"DynamoDB 데이터 삭제 성공 - user_id: {user_id}, asset_id: {asset_id}")
        logger.info(f"DynamoDB 삭제 응답: {response}")
        return True
    except ClientError as e:
        logger.error(f"DynamoDB 삭제 중 오류 발생: {str(e)}")
        return False

async def get_asset_metadata(user_id: str, asset_id: str) -> Optional[Dict[str, Any]]:
    """자산 메타데이터를 DynamoDB에서 조회"""
    try:
        loop = asyncio.get_event_loop()
        return await loop.run_in_executor(None, _get_asset_metadata_sync, user_id, asset_id)
    except Exception as e:
        logger.error(f"DynamoDB 비동기 조회 중 오류 발생: {str(e)}")
        return None

def _get_asset_metadata_sync(user_id: str, asset_id: str) -> Optional[Dict[str, Any]]:
    """자산 메타데이터를 DynamoDB에서 조회 (동기 버전)"""
    try:
        dynamodb = get_dynamodb_client()
        response = dynamodb.get_item(
            TableName=settings.DYNAMODB_TABLE_NAME,
            Key={
                'user_id': {'S': str(user_id)},
                'asset_id': {'S': str(asset_id)}
            }
        )
        
        if 'Item' not in response:
            return None

        item = response['Item']
        result = {
            'user_id': item['user_id']['S'],
            'asset_id': item['asset_id']['S'],
            'name': item['name']['S'],
            'mime_type': item['mime_type']['S'],
            'size': int(item['size']['N']),
            's3_key': item['s3_key']['S'],
            'created_at': item['created_at']['S']
        }

        if 'description' in item:
            result['description'] = item['description']['S']
        if 'folder_id' in item:
            result['folder_id'] = item['folder_id']['S']
        if 'tags' in item:
            result['tags'] = [{
                'id': tag['M']['id']['S'],
                'name': tag['M']['name']['S']
            } for tag in item['tags']['L']]

        return result
    except ClientError as e:
        logger.error(f"DynamoDB 조회 중 오류 발생: {str(e)}")
        return None 