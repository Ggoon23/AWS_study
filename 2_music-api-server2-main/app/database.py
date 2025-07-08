from typing import List, Optional
from .models import Music

class Database:
    def __init__(self):
        self.music_list: List[Music] = [
            Music(
                id=1,
                title="Dynamite",
                artist="BTS",
                album="BE",
                release_year=2020,
                genre="K-pop",
                duration=199,
                likes=1000000
            ),
            Music(
                id=2,
                title="Spring Day",
                artist="BTS",
                album="You Never Walk Alone",
                release_year=2017,
                genre="K-pop",
                duration=255,
                likes=950000
            ),
            Music(
                id=3,
                title="How You Like That",
                artist="BLACKPINK",
                album="THE ALBUM",
                release_year=2020,
                genre="K-pop",
                duration=182,
                likes=890000
            ),
            Music(
                id=4,
                title="Maria",
                artist="Hwasa",
                album="Maria",
                release_year=2020,
                genre="K-pop",
                duration=195,
                likes=450000
            ),
            Music(
                id=5,
                title="Celebrity",
                artist="IU",
                album="Celebrity",
                release_year=2021,
                genre="K-pop",
                duration=195,
                likes=780000
            )
        ]

    def get_all_music(self) -> List[Music]:
        return self.music_list

    def get_music_by_id(self, music_id: int) -> Optional[Music]:
        return next((music for music in self.music_list if music.id == music_id), None)

    def get_music_by_genre(self, genre: str) -> List[Music]:
        return [music for music in self.music_list if music.genre.lower() == genre.lower()]

    def add_like(self, music_id: int) -> bool:
        music = self.get_music_by_id(music_id)
        if music:
            music.likes += 1
            return True
        return False

db = Database() 