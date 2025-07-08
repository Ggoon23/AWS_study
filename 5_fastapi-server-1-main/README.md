# 디지털 자산 관리 플랫폼 (Digital Asset Management Platform)

안전하고 효율적인 디지털 자산 관리를 위한 클라우드 기반 솔루션입니다.

## 개발 환경 설정

### 요구사항
- Python 3.10
- MySQL 8.0
- AWS 계정 (S3, DynamoDB 사용)
- EC2 인스턴스 접속 정보 (프라이빗 RDS 접근용)

### 프라이빗 RDS 접근 설정

1. SSH 터널링 설정
```bash
# EC2를 통한 RDS 포트 포워딩
# -f: 백그라운드 실행
# -N: 명령어 실행하지 않음
# -L: 로컬 포트 포워딩 (local_port:rds_endpoint:rds_port)
# ssh -f -N -L 3306:your_rds_endpoint:3306 -i your_key.pem ec2-user@your_ec2_public_ip
ssh -N -L 3306:your_rds_endpoint:3306 -i your_key.pem ec2-user@your_ec2_public_ip

# 예시
ssh -N -L 3306:my-database.xxxxx.region.rds.amazonaws.com:3306 -i ~/.ssh/my-key.pem ec2-user@11.22.33.44

# 백그라운드로 실행한 경우만 - 터널링 상태 확인
ps aux | grep ssh

# 터널링 종료 (필요시)
# 프로세스 ID 찾기
ps aux | grep ssh
# 종료
kill <process_id>
```

2. 터널링 연결 테스트
```bash
# MySQL 클라이언트로 테스트 (설치되어 있는 경우)
mysql -h 127.0.0.1 -P 3306 -u your_db_user -p

# 또는 Python에서 테스트
python -c "import mysql.connector; mysql.connector.connect(host='127.0.0.1', port=3306, user='your_db_user', password='your_password', database='your_db')"
```

### 로컬 개발 환경 설정

1. Python 3.10 설치
```bash
# macOS (Homebrew)
brew install python@3.10

# Ubuntu/Debian
sudo apt update
sudo apt install python3.10 python3.10-venv python3.10-dev

# Windows
# Python 3.10을 https://www.python.org/downloads/ 에서 다운로드 후 설치
```

2. 가상환경 생성 및 활성화
```bash
# 기존 가상환경이 있다면 제거
rm -rf venv

# 새 가상환경 생성
python3.10 -m venv venv

# 가상환경 활성화
# Windows
venv\\Scripts\\activate
# macOS/Linux
source venv/bin/activate
```

3. pip 업그레이드 및 캐시 정리
```bash
# pip 업그레이드
python -m pip install --upgrade pip

# pip 캐시 정리
pip cache purge

# 기존 패키지 제거
pip uninstall -y pydantic pydantic-core pydantic-settings
```

4. 의존성 설치
```bash
# 필수 패키지 먼저 설치
pip install --no-deps -r requirements.txt

# 나머지 의존성 설치
pip install -r requirements.txt
```

5. 환경 변수 설정
- `.env.local` 파일을 생성하고 필요한 환경 변수 설정
```bash
# Database (SSH 터널링 사용시)
DB_HOST=127.0.0.1  # localhost 대신 127.0.0.1 사용
DB_PORT=3306
DB_USER=your_db_user
DB_PASSWORD=your_db_password
DB_NAME=digital_asset_db

# JWT
JWT_SECRET_KEY=your_secret_key
JWT_ALGORITHM=HS256
ACCESS_TOKEN_EXPIRE_MINUTES=30

# AWS Credentials
AWS_ACCESS_KEY_ID=your_access_key_id
AWS_SECRET_ACCESS_KEY=your_secret_access_key
AWS_REGION=ap-northeast-2

# AWS Services
S3_BUCKET_NAME=your_bucket_name
DYNAMODB_TABLE_NAME=AssetMetadata
```

6. 서버 실행
```bash
# 기본 실행 (개발 모드)
python run.py --reload

# 특정 포트로 실행
python run.py --port 8080

# 프로덕션 모드로 실행 (다중 워커)
python run.py --workers 4

# 전체 옵션 보기
python run.py --help
```

실행 옵션:
- `--host`: 바인딩할 호스트 주소 (기본값: 0.0.0.0)
- `--port`: 사용할 포트 번호 (기본값: 8000)
- `--reload`: 코드 변경 시 자동 재시작 (개발 모드)
- `--workers`: 워커 프로세스 수 (기본값: 1)

API 문서: http://localhost:8000/docs

### 문제 해결

1. pydantic 관련 오류가 발생하는 경우:
```bash
pip uninstall -y pydantic pydantic-core pydantic-settings
pip install pydantic==2.8.0 pydantic-core==2.33.2 pydantic-settings==2.4.0
```

2. certifi 관련 오류가 발생하는 경우:
```bash
pip install --upgrade "certifi>=2024.7.4,<2025.0.0"
```

3. 의존성 충돌이 계속되는 경우:
```bash
# 가상환경을 새로 만들고 처음부터 다시 시작
deactivate  # 현재 가상환경 비활성화
rm -rf venv  # 가상환경 삭제
python3.10 -m venv venv  # 새 가상환경 생성
source venv/bin/activate  # 가상환경 활성화
pip install --upgrade pip  # pip 업그레이드
pip install -r requirements.txt  # 의존성 설치
```

4. RDS 연결 문제 해결:
```bash
# 터널링 상태 확인
ps aux | grep ssh

# 터널링이 끊어진 경우 재연결
ssh -f -N -L 3306:your_rds_endpoint:3306 -i your_key.pem ec2-user@your_ec2_public_ip

# 연결 테스트
nc -zv 127.0.0.1 3306

# 방화벽 설정 확인
# EC2 보안 그룹: 로컬 IP에서 22번 포트 접근 허용
# RDS 보안 그룹: EC2 보안 그룹에서 3306 포트 접근 허용
```

5. SSH 터널링 자동화 스크립트 (옵션)
```bash
#!/bin/bash
# tunnel.sh
RDS_ENDPOINT="your_rds_endpoint"
EC2_IP="your_ec2_public_ip"
KEY_PATH="path_to_your_key.pem"
LOCAL_PORT=3306
RDS_PORT=3306

# 기존 터널링 확인 및 종료
pid=$(ps aux | grep "ssh -f -N -L $LOCAL_PORT:$RDS_ENDPOINT:$RDS_PORT" | grep -v grep | awk '{print $2}')
if [ ! -z "$pid" ]; then
    echo "Killing existing tunnel (PID: $pid)"
    kill $pid
fi

# 새 터널링 시작
echo "Starting new SSH tunnel..."
ssh -f -N -L $LOCAL_PORT:$RDS_ENDPOINT:$RDS_PORT -i $KEY_PATH ec2-user@$EC2_IP

# 상태 확인
if [ $? -eq 0 ]; then
    echo "SSH tunnel established successfully"
    echo "Database is now accessible at localhost:$LOCAL_PORT"
else
    echo "Failed to establish SSH tunnel"
fi
```

사용 방법:
```bash
# 스크립트 실행 권한 부여
chmod +x tunnel.sh

# 터널링 시작
./tunnel.sh
```

### AWS 서비스 설정

1. DynamoDB 테이블 생성
```bash
# 테이블 생성
python -m app.utils.dynamodb_setup create

# 테이블 삭제 (필요한 경우)
python -m app.utils.dynamodb_setup delete
```

테이블 설정:
- 테이블 이름: `AssetMetadata` (또는 .env에서 설정한 이름)
- 파티션 키: `user_id` (String)
- 정렬 키: `asset_id` (String)
- GSI: `UserCreatedAtIndex`
  - 파티션 키: `user_id`
  - 정렬 키: `created_at`
- 프로비저닝된 용량: 읽기/쓰기 각 5 유닛

## 주요 기능

### 1. 체계적인 파일 관리
- 폴더 구조를 통한 체계적인 파일 정리
- 직관적인 폴더 트리 구조
- 드래그 앤 드롭으로 쉬운 파일 이동

### 2. 강력한 태그 시스템
- 파일에 다중 태그 지정 가능
- 태그 기반 빠른 검색
- 자주 사용하는 태그 즐겨찾기

### 3. 안전한 파일 저장
- AWS S3 기반의 안전한 파일 저장
- 파일 버전 관리
- 자동 백업

### 4. 편리한 접근성
- 웹 기반 인터페이스로 어디서나 접근 가능
- 직관적인 사용자 인터페이스
- 빠른 파일 업로드/다운로드

## 시작하기

### 1. 회원가입
- 이메일과 비밀번호로 간단히 가입
- 소셜 로그인 지원 예정

### 2. 폴더 구성
```bash
# 새 폴더 생성
POST /api/v1/folders
{
    "name": "프로젝트 문서",
    "parent_id": null  # 루트 폴더로 생성
}

# 하위 폴더 생성
POST /api/v1/folders
{
    "name": "디자인 파일",
    "parent_id": "상위_폴더_ID"
}
```

### 3. 파일 업로드
- 드래그 앤 드롭으로 간단히 업로드
- 여러 파일 동시 업로드 가능
- 파일 설명과 태그 추가 가능
```bash
POST /api/v1/assets
Form-Data:
- file: 파일
- name: "프로젝트 기획서"
- description: "2024년 프로젝트 기획서"
- folder_id: "폴더_ID"
- tags: ["기획", "2024", "프로젝트"]
```

### 4. 파일 관리
- 폴더별 파일 목록 조회
```bash
GET /api/v1/assets?folder_id=폴더_ID
```

- 태그로 파일 검색
```bash
GET /api/v1/assets?tag=기획
```

## 사용 예시

### 프로젝트 문서 관리
1. "프로젝트" 폴더 생성
2. 하위에 "기획", "디자인", "개발" 폴더 생성
3. 각 폴더에 관련 파일 업로드
4. 태그를 활용하여 프로젝트별, 단계별 파일 구분

### 디자인 에셋 관리
1. "디자인" 폴더 아래 프로젝트별 하위 폴더 생성
2. 작업 파일 업로드 시 버전 태그 추가
3. 태그 검색으로 최신 버전 파일 빠르게 찾기

### 문서 자료 관리
1. 부서별/프로젝트별 폴더 구조 생성
2. 문서 업로드 시 관련 태그 추가
3. 폴더 구조와 태그 검색으로 필요한 문서 빠르게 찾기

## 보안 및 권한

- 모든 파일은 암호화되어 저장
- 사용자별 독립적인 파일 관리 공간
- AWS 기반의 안전한 인프라 활용

## 향후 업데이트 예정 기능

1. 팀 협업 기능
   - 팀 공유 폴더
   - 멤버별 권한 관리
   - 실시간 협업 기능

2. 고급 검색 기능
   - 파일 내용 검색
   - AI 기반 이미지 검색
   - 메타데이터 기반 필터링

3. 자동화 기능
   - 자동 태그 추천
   - 파일 분류 자동화
   - 중복 파일 감지

4. 모바일 앱
   - iOS/Android 앱 출시
   - 모바일 최적화 인터페이스
   - 푸시 알림 지원