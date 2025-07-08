from pydantic import BaseModel
from datetime import datetime
from typing import Optional, List

class TagBase(BaseModel):
    name: str

class TagCreate(TagBase):
    pass

class TagResponse(TagBase):
    id: int
    created_at: datetime

    class Config:
        from_attributes = True

class AssetBase(BaseModel):
    name: str
    description: Optional[str] = None
    folder_id: Optional[int] = None

class AssetCreate(AssetBase):
    tags: Optional[List[str]] = []

class AssetUpdate(AssetBase):
    tags: Optional[List[str]] = []

class AssetResponse(AssetBase):
    id: int
    mime_type: str
    size: int
    file_url: str  # S3 URL
    created_at: datetime
    updated_at: datetime
    tags: List[TagResponse] = []

    class Config:
        from_attributes = True 