from fastapi import FastAPI, HTTPException, Depends
from sqlalchemy.orm import Session
from typing import List
from . import models, database
from .database import get_db

app = FastAPI(
    title="MelodyHub",
    description="음악 정보를 제공하는 API 서버",
    version="1.0.0"
)

@app.get("/api/music", response_model=List[models.Music])
async def get_all_music(db: Session = Depends(get_db)):
    """
    모든 음악 목록을 반환합니다.
    """
    music_list = db.query(models.MusicDB).all()
    return music_list

@app.get("/api/music/{music_id}", response_model=models.Music)
async def get_music_by_id(music_id: int, db: Session = Depends(get_db)):
    """
    특정 ID의 음악 정보를 반환합니다.
    """
    music = db.query(models.MusicDB).filter(models.MusicDB.id == music_id).first()
    if not music:
        raise HTTPException(status_code=404, detail="음악을 찾을 수 없습니다.")
    return music

@app.get("/api/music/genre/{genre}", response_model=List[models.Music])
async def get_music_by_genre(genre: str, db: Session = Depends(get_db)):
    """
    특정 장르의 음악 목록을 반환합니다.
    """
    music_list = db.query(models.MusicDB).filter(models.MusicDB.genre.ilike(f"%{genre}%")).all()
    return music_list

@app.post("/api/music/{music_id}/like")
async def add_like(music_id: int, db: Session = Depends(get_db)):
    """
    특정 음악의 좋아요 수를 증가시킵니다.
    """
    music = db.query(models.MusicDB).filter(models.MusicDB.id == music_id).first()
    if not music:
        raise HTTPException(status_code=404, detail="음악을 찾을 수 없습니다.")
    
    music.likes += 1
    db.commit()
    return {"message": "좋아요가 추가되었습니다."} 