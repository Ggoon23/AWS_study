from pydantic import BaseModel
from datetime import datetime
from typing import Optional, List

class FolderBase(BaseModel):
    name: str
    parent_id: Optional[int] = None

class FolderCreate(FolderBase):
    pass

class FolderUpdate(FolderBase):
    pass

class FolderResponse(FolderBase):
    id: int
    user_id: int
    created_at: datetime
    updated_at: datetime

    class Config:
        from_attributes = True

class FolderTreeResponse(FolderResponse):
    children: List['FolderTreeResponse'] = []

    class Config:
        from_attributes = True 