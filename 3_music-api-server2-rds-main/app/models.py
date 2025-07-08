from sqlalchemy import Column, Integer, String
from sqlalchemy.orm import Mapped
from pydantic import BaseModel
from .database import Base

# Pydantic 모델 (API 응답용)
class MusicBase(BaseModel):
    title: str
    artist: str
    album: str
    release_year: int
    genre: str
    duration: int
    likes: int

class Music(MusicBase):
    id: int

    class Config:
        from_attributes = True

# SQLAlchemy 모델
class MusicDB(Base):
    __tablename__ = "music"

    id: Mapped[int] = Column(Integer, primary_key=True, index=True)
    title: Mapped[str] = Column(String(100), nullable=False)
    artist: Mapped[str] = Column(String(100), nullable=False)
    album: Mapped[str] = Column(String(100), nullable=False)
    release_year: Mapped[int] = Column(Integer, nullable=False)
    genre: Mapped[str] = Column(String(50), nullable=False)
    duration: Mapped[int] = Column(Integer, nullable=False)
    likes: Mapped[int] = Column(Integer, nullable=False, default=0) 