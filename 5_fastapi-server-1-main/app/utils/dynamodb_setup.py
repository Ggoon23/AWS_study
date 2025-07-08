import boto3
from botocore.exceptions import ClientError
from app.config import settings

def create_asset_metadata_table():
    """AssetMetadata 테이블 생성"""
    dynamodb = boto3.client(
        'dynamodb',
        aws_access_key_id=settings.AWS_ACCESS_KEY_ID,
        aws_secret_access_key=settings.AWS_SECRET_ACCESS_KEY,
        region_name=settings.AWS_REGION
    )

    table_name = settings.DYNAMODB_TABLE_NAME

    # 테이블이 이미 존재하는지 확인
    try:
        dynamodb.describe_table(TableName=table_name)
        print(f"테이블 '{table_name}'이(가) 이미 존재합니다.")
        return
    except ClientError as e:
        if e.response['Error']['Code'] != 'ResourceNotFoundException':
            raise

    # 테이블 생성
    try:
        response = dynamodb.create_table(
            TableName=table_name,
            KeySchema=[
                {
                    'AttributeName': 'user_id',
                    'KeyType': 'HASH'
                },
                {
                    'AttributeName': 'asset_id',
                    'KeyType': 'RANGE'
                }
            ],
            AttributeDefinitions=[
                {
                    'AttributeName': 'user_id',
                    'AttributeType': 'S'
                },
                {
                    'AttributeName': 'asset_id',
                    'AttributeType': 'S'
                },
                {
                    'AttributeName': 'created_at',
                    'AttributeType': 'S'
                }
            ],
            GlobalSecondaryIndexes=[
                {
                    'IndexName': 'UserCreatedAtIndex',
                    'KeySchema': [
                        {
                            'AttributeName': 'user_id',
                            'KeyType': 'HASH'
                        },
                        {
                            'AttributeName': 'created_at',
                            'KeyType': 'RANGE'
                        }
                    ],
                    'Projection': {
                        'ProjectionType': 'ALL'
                    },
                    'ProvisionedThroughput': {
                        'ReadCapacityUnits': 5,
                        'WriteCapacityUnits': 5
                    }
                }
            ],
            ProvisionedThroughput={
                'ReadCapacityUnits': 5,
                'WriteCapacityUnits': 5
            }
        )
        print(f"테이블 '{table_name}'이(가) 생성되었습니다.")
        print("테이블 상태:", response['TableDescription']['TableStatus'])
        
        # 테이블이 활성화될 때까지 대기
        waiter = dynamodb.get_waiter('table_exists')
        waiter.wait(
            TableName=table_name,
            WaiterConfig={'Delay': 5, 'MaxAttempts': 20}
        )
        print(f"테이블 '{table_name}'이(가) 활성화되었습니다.")
        
    except ClientError as e:
        print(f"테이블 생성 중 오류 발생: {e}")
        raise

def delete_asset_metadata_table():
    """AssetMetadata 테이블 삭제 (필요한 경우)"""
    dynamodb = boto3.client(
        'dynamodb',
        aws_access_key_id=settings.AWS_ACCESS_KEY_ID,
        aws_secret_access_key=settings.AWS_SECRET_ACCESS_KEY,
        region_name=settings.AWS_REGION
    )

    table_name = settings.DYNAMODB_TABLE_NAME

    try:
        dynamodb.delete_table(TableName=table_name)
        print(f"테이블 '{table_name}'이(가) 삭제되었습니다.")
        
        # 테이블이 삭제될 때까지 대기
        waiter = dynamodb.get_waiter('table_not_exists')
        waiter.wait(
            TableName=table_name,
            WaiterConfig={'Delay': 5, 'MaxAttempts': 20}
        )
        print(f"테이블 '{table_name}'이(가) 완전히 삭제되었습니다.")
        
    except ClientError as e:
        if e.response['Error']['Code'] == 'ResourceNotFoundException':
            print(f"테이블 '{table_name}'이(가) 존재하지 않습니다.")
        else:
            print(f"테이블 삭제 중 오류 발생: {e}")
            raise

if __name__ == "__main__":
    import argparse
    
    parser = argparse.ArgumentParser(description='DynamoDB 테이블 관리')
    parser.add_argument('action', choices=['create', 'delete'], help='수행할 작업')
    
    args = parser.parse_args()
    
    if args.action == 'create':
        create_asset_metadata_table()
    else:
        delete_asset_metadata_table() 