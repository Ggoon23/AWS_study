
# MelodyHub - FastAPI 음악 정보 API 서버

## 소개
**MelodyHub**는 음악 정보를 제공하는 간단한 FastAPI 기반 REST API 서버입니다.  
DB 없이 페이크 데이터를 사용하며, 음악 리스트, 상세 정보, 장르별 검색, 좋아요 기능을 제공합니다.

## 주요 기능(API)
- **음악 리스트 조회**  
  `GET /api/music`
- **음악 상세 정보 조회**  
  `GET /api/music/{id}`
- **장르별 음악 검색**  
  `GET /api/music/genre/{genre}`
- **음악 좋아요 추가**  
  `POST /api/music/{id}/like`

## 실행 방법

### 1. 의존성 설치
```bash
pip install -r requirements.txt
```

### 2. 서버 실행
#### 방법 1: run.py 사용
```bash
python run.py
```
#### 방법 2: uvicorn 명령어 직접 사용
```bash
uvicorn app.main:app --reload
```

### 3. API 문서 확인
- Swagger UI: [http://localhost:8000/docs](http://localhost:8000/docs)
- ReDoc: [http://localhost:8000/redoc](http://localhost:8000/redoc)

## 예시 데이터
- BTS, BLACKPINK, IU 등 K-pop 음악 5곡이 기본 데이터로 제공됩니다.

## 예시 요청
```bash
# 모든 음악 목록 조회
curl http://localhost:8000/api/music

# 특정 음악 상세 정보
curl http://localhost:8000/api/music/1

# 장르별 음악 검색
curl http://localhost:8000/api/music/genre/K-pop

# 좋아요 추가
curl -X POST http://localhost:8000/api/music/1/like
```

## Python 버전
- Python 3.10.x 권장

