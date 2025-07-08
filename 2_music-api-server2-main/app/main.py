from fastapi import FastAPI, HTTPException
from typing import List
from .models import Music
from .database import db

app = FastAPI(
    title="MelodyHub",
    description="음악 정보를 제공하는 API 서버",
    version="1.0.0"
)

@app.get("/api/music", response_model=List[Music])
async def get_all_music():
    """
    모든 음악 목록을 반환합니다.
    """
    return db.get_all_music()

@app.get("/api/music/{music_id}", response_model=Music)
async def get_music_by_id(music_id: int):
    """
    특정 ID의 음악 정보를 반환합니다.
    """
    music = db.get_music_by_id(music_id)
    if not music:
        raise HTTPException(status_code=404, detail="음악을 찾을 수 없습니다.")
    return music

@app.get("/api/music/genre/{genre}", response_model=List[Music])
async def get_music_by_genre(genre: str):
    """
    특정 장르의 음악 목록을 반환합니다.
    """
    return db.get_music_by_genre(genre)

@app.post("/api/music/{music_id}/like")
async def add_like(music_id: int):
    """
    특정 음악의 좋아요 수를 증가시킵니다.
    """
    success = db.add_like(music_id)
    if not success:
        raise HTTPException(status_code=404, detail="음악을 찾을 수 없습니다.")
    return {"message": "좋아요가 추가되었습니다."} 