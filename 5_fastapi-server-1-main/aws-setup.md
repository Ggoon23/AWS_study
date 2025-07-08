# AWS 인프라 구성 가이드

## VPC 구성

1. VPC 생성
   - CIDR: 10.0.0.0/16
   - 가용영역: ap-northeast-2a, ap-northeast-2c
   - 퍼블릭 서브넷: 10.0.1.0/24, 10.0.2.0/24
   - 프라이빗 서브넷: 10.0.11.0/24, 10.0.12.0/24

2. 인터넷 게이트웨이 생성 및 연결
   - VPC에 인터넷 게이트웨이 연결
   - 퍼블릭 서브넷의 라우팅 테이블에 인터넷 게이트웨이 추가

3. NAT 게이트웨이 생성
   - 퍼블릭 서브넷에 NAT 게이트웨이 생성
   - 프라이빗 서브넷의 라우팅 테이블에 NAT 게이트웨이 추가

## RDS (MySQL) 구성

1. 서브넷 그룹 생성
   - 프라이빗 서브넷들을 포함하는 DB 서브넷 그룹 생성

2. RDS 인스턴스 생성
   - 엔진: MySQL 8.0
   - 인스턴스 클래스: db.t3.micro (개발용)
   - 스토리지: 20GB gp2
   - 다중 AZ: 아니오 (개발용)
   - VPC: 생성한 VPC 선택
   - 서브넷 그룹: 생성한 DB 서브넷 그룹
   - 퍼블릭 액세스: 아니오
   - 보안 그룹: 새로 생성 (EC2의 보안 그룹에서만 접근 허용)

## S3 구성

1. 버킷 생성
   - 리전: ap-northeast-2
   - 버킷 이름: 고유한 이름 지정
   - 퍼블릭 액세스 차단: 활성화
   - 버저닝: 활성화
   - 암호화: SSE-S3

2. CORS 설정
   ```json
   [
       {
           "AllowedHeaders": ["*"],
           "AllowedMethods": ["GET", "PUT", "POST", "DELETE"],
           "AllowedOrigins": ["*"],
           "ExposeHeaders": []
       }
   ]
   ```

## DynamoDB 구성

1. 테이블 생성
   - 테이블 이름: AssetMetadata
   - 파티션 키: user_id (String)
   - 정렬 키: asset_id (String)
   - 용량 모드: 온디맨드
   - 암호화: 활성화

2. GSI 생성
   - 인덱스 이름: UserCreatedAtIndex
   - 파티션 키: user_id (String)
   - 정렬 키: created_at (String)
   - 프로젝션 유형: ALL

## EC2 구성

1. EC2 인스턴스 생성
   - AMI: Amazon Linux 2
   - 인스턴스 유형: t2.micro (개발용)
   - VPC: 생성한 VPC
   - 서브넷: 퍼블릭 서브넷
   - 퍼블릭 IP 자동 할당: 활성화
   - 보안 그룹: 새로 생성
     - SSH (22): 관리자 IP
     - HTTP (80): 0.0.0.0/0
     - HTTPS (443): 0.0.0.0/0

2. IAM 역할 생성 및 연결
   - S3 접근 권한 (AmazonS3FullAccess)
   - DynamoDB 접근 권한 (AmazonDynamoDBFullAccess)


# 테이블 생성
python -m app.utils.dynamodb_setup create

# 테이블 삭제
python -m app.utils.dynamodb_setup delete


## 보안 그룹 구성

1. EC2 보안 그룹
   ```
   인바운드:
   - SSH (22): 관리자 IP
   - HTTP (80): 0.0.0.0/0
   - HTTPS (443): 0.0.0.0/0
   ```

2. RDS 보안 그룹
   ```
   인바운드:
   - MySQL (3306): EC2 보안 그룹
   ```

## 배포 프로세스

1. EC2 인스턴스 초기 설정
   ```bash
   # 시스템 업데이트
   sudo yum update -y
   
   # Python 3.9 설치
   sudo yum install python39 python39-devel
   
   # pip 설치
   curl -O https://bootstrap.pypa.io/get-pip.py
   python3 get-pip.py
   
   # 가상환경 생성
   python3 -m venv venv
   source venv/bin/activate
   
   # 의존성 설치
   pip install -r requirements.txt
   ```

2. 애플리케이션 배포
   ```bash
   # 환경 변수 설정
   export DB_HOST=<RDS_ENDPOINT>
   export DB_PORT=3306
   export DB_USER=<DB_USER>
   export DB_PASSWORD=<DB_PASSWORD>
   export DB_NAME=digital_asset_db
   export JWT_SECRET_KEY=<YOUR_SECRET_KEY>
   export AWS_REGION=ap-northeast-2
   export S3_BUCKET_NAME=<YOUR_BUCKET_NAME>
   export DYNAMODB_TABLE_NAME=AssetMetadata
   
   # 서버 실행
   uvicorn app.main:app --host 0.0.0.0 --port 80
   ```

3. (선택사항) Nginx 설정
   ```bash
   # Nginx 설치
   sudo yum install nginx
   
   # Nginx 설정
   sudo vi /etc/nginx/conf.d/app.conf
   ```
   ```nginx
   server {
       listen 80;
       server_name your_domain.com;
   
       location / {
           proxy_pass http://localhost:8000;
           proxy_set_header Host $host;
           proxy_set_header X-Real-IP $remote_addr;
       }
   }
   ``` 