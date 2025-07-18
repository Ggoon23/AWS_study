# AWS_study

AWS 실습을 위한 다양한 서버 환경의 더미(Dummy) 서버 파일

## 사용 및 배포 가이드
### 🖥️ 로컬 테스트
서버 코드를 클론한 뒤, 로컬 환경에서 실행 및 기능을 점검

### ☁️ AWS EC2 배포
테스트를 마친 서버를 EC2 인스턴스에 배포
보안 그룹 설정을 통해 외부에서 접근 가능한 포트를 개방

### 🌐 접근 방법
브라우저 또는 API 클라이언트에서
http://<EC2 퍼블릭 IPv4 주소>:<포트번호>
형태로 서버에 접속가능

### ⚙️ 환경 구성
데이터베이스(RDS), 파일 스토리지(S3) 연동이 필요한 서버는 환경 변수 또는 설정 파일을 통해 별도 구성

### ⚠️ 유의 사항
1) 본 서버들은 실제 서비스가 아닌 테스트 및 구조 확인 목적의 더미 서버입니다.<br/>
2) 배포 후, 할당된 퍼블릭 IPv4 주소와 포트 정보를 반드시 확인해야 합니다.

## 주요 기술 스택
☕ Java SpringBoot
🐍 Python FastAPI
🗄️ RDS
🗂️ S3

## 서버 목록
1_음악 정보 제공 서버 (Java SpringBoot)

2_음악 정보 제공 서버 (Python FastAPI)

3_음악 정보 제공 서버 (FastAPI + RDS)

4_상품 리뷰 서버 (SpringBoot + RDS)

5_음식점 리뷰 서버 (SpringBoot + RDS + S3)

6_디지털 자산 관리 서버 (FastAPI + SpringBoot + RDS + S3)
