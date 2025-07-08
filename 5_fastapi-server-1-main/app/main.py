from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
import logging

# 로깅 설정
logging.basicConfig(
    level=logging.DEBUG,  # INFO에서 DEBUG로 변경
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    handlers=[
        logging.StreamHandler()  # 콘솔에 출력
    ]
)

# AWS SDK 로깅 설정
logging.getLogger('boto3').setLevel(logging.DEBUG)
logging.getLogger('botocore').setLevel(logging.DEBUG)
logging.getLogger('urllib3').setLevel(logging.DEBUG)

app = FastAPI(
    title="Digital Asset Management API",
    description="API for managing digital assets",
    version="1.0.0"
)

# CORS 설정
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # 프로덕션에서는 실제 프론트엔드 도메인으로 변경
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.get("/")
async def root():
    return {"message": "Digital Asset Management API"}

# 라우터 등록
from app.routers import auth, assets, folders
app.include_router(auth.router, prefix="/api/v1/auth", tags=["auth"])
app.include_router(assets.router, prefix="/api/v1/assets", tags=["assets"])
app.include_router(folders.router, prefix="/api/v1/folders", tags=["folders"]) 