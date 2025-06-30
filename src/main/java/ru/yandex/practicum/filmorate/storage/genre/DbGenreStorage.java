package ru.yandex.practicum.filmorate.storage.genre;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Repository("dbGenreStorage")
@RequiredArgsConstructor
@Getter
public class DbGenreStorage implements GenreStorage {
    private final GenreRepository genreRepository;

    @Override
    public Optional<Genre> getGenreById(Integer id) {
        return genreRepository.getGenreById(id);
    }

    @Override
    public Collection<Genre> findAll() {
        return genreRepository.findAll();
    }
}
