from sqlalchemy import Column, String, DateTime, ForeignKey, func, BigInteger
from sqlalchemy.orm import relationship
from app.database import Base
import uuid

class Folder(Base):
    __tablename__ = "folders"

    id = Column(BigInteger, primary_key=True, autoincrement=True)
    name = Column(String(255), nullable=False)
    parent_id = Column(BigInteger, ForeignKey("folders.id"), nullable=True)
    user_id = Column(BigInteger, ForeignKey("users.id"), nullable=False)
    created_at = Column(DateTime, server_default=func.now())
    updated_at = Column(DateTime, server_default=func.now(), onupdate=func.now())

    # Relationships
    parent = relationship("Folder", remote_side=[id], back_populates="children")
    children = relationship("Folder", back_populates="parent")
    assets = relationship("Asset", back_populates="folder") 