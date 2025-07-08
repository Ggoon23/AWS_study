import boto3
from botocore.exceptions import ClientError
from app.config import settings
from fastapi import UploadFile
import uuid

s3_client = boto3.client(
    's3',
    aws_access_key_id=settings.AWS_ACCESS_KEY_ID,
    aws_secret_access_key=settings.AWS_SECRET_ACCESS_KEY,
    region_name=settings.AWS_REGION
)

def generate_presigned_url(s3_key: str, expiration: int = 3600) -> str:
    """
    Generate a presigned URL for accessing an S3 object
    """
    try:
        url = s3_client.generate_presigned_url(
            'get_object',
            Params={
                'Bucket': settings.S3_BUCKET_NAME,
                'Key': s3_key
            },
            ExpiresIn=expiration
        )
        return url
    except ClientError as e:
        print(e)
        return None

async def upload_file(file: UploadFile, folder_path: str = "") -> tuple[str, int]:
    """
    Upload a file to S3 and return the S3 key and file size
    """
    # Generate unique file name
    file_extension = file.filename.split('.')[-1] if '.' in file.filename else ''
    s3_key = f"{folder_path}/{str(uuid.uuid4())}.{file_extension}".strip('/')
    
    # Read file content
    content = await file.read()
    file_size = len(content)
    
    try:
        s3_client.put_object(
            Bucket=settings.S3_BUCKET_NAME,
            Key=s3_key,
            Body=content,
            ContentType=file.content_type
        )
        return s3_key, file_size
    except ClientError as e:
        print(e)
        return None, 0

def delete_file(s3_key: str) -> bool:
    """
    Delete a file from S3
    """
    try:
        s3_client.delete_object(
            Bucket=settings.S3_BUCKET_NAME,
            Key=s3_key
        )
        return True
    except ClientError as e:
        print(e)
        return False 