package ru.yandex.practicum.filmorate.storage.genre;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

@Slf4j
@Repository("memoryGenreStorage")
@Getter
public class InMemoryGenreStorage implements GenreStorage {

    private final HashMap<Integer, Genre> genres = new HashMap<>();

    public InMemoryGenreStorage() {
        init();
    }

    private void init() {
        genres.put(1, new Genre(1, "Комедия"));
        genres.put(2, new Genre(2, "Драма"));
        genres.put(3, new Genre(3, "Мультфильм"));
        genres.put(4, new Genre(4, "Триллер"));
        genres.put(5, new Genre(5, "Документальный"));
        genres.put(6, new Genre(6, "Боевик"));
    }

    @Override
    public Optional<Genre> getGenreById(Integer id) {
        if (!genres.containsKey(id)) return Optional.empty();
        return Optional.of(genres.get(id));
    }

    @Override
    public Collection<Genre> findAll() {
        return genres.values();
    }
}
