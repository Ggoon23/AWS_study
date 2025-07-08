from pydantic import BaseModel

class Music(BaseModel):
    id: int
    title: str
    artist: str
    album: str
    release_year: int
    genre: str
    duration: int  # in seconds
    likes: int 