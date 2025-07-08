from sqlalchemy import Column, String, DateTime, ForeignKey, func, BigInteger, Text, Table
from sqlalchemy.orm import relationship
from app.database import Base
import uuid

# 자산-태그 관계 테이블
asset_tags = Table(
    'asset_tags',
    Base.metadata,
    Column('asset_id', BigInteger, ForeignKey('assets.id'), primary_key=True),
    Column('tag_id', BigInteger, ForeignKey('tags.id'), primary_key=True)
)

class Asset(Base):
    __tablename__ = "assets"

    id = Column(BigInteger, primary_key=True, autoincrement=True)
    name = Column(String(255), nullable=False)
    description = Column(Text, nullable=True)
    mime_type = Column(String(100), nullable=False)
    size = Column(BigInteger, nullable=False)
    s3_key = Column(String(255), nullable=False)
    folder_id = Column(BigInteger, ForeignKey("folders.id"), nullable=True)
    user_id = Column(BigInteger, ForeignKey("users.id"), nullable=False)
    created_at = Column(DateTime, server_default=func.now())
    updated_at = Column(DateTime, server_default=func.now(), onupdate=func.now())

    # Relationships
    folder = relationship("Folder", back_populates="assets")
    tags = relationship("Tag", secondary=asset_tags, back_populates="assets")

class Tag(Base):
    __tablename__ = "tags"

    id = Column(BigInteger, primary_key=True, autoincrement=True)
    name = Column(String(100), nullable=False)
    user_id = Column(BigInteger, ForeignKey("users.id"), nullable=False)
    created_at = Column(DateTime, server_default=func.now())

    # Relationships
    assets = relationship("Asset", secondary=asset_tags, back_populates="tags") 