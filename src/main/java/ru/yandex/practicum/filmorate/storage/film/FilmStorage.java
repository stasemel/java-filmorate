package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    Optional<Film> createFilm(Film film);

    Optional<Film> updateFilm(Film film);

    boolean isNotDuplicate(Film film);

    Collection<Film> findAll();

    Optional<Film> getFilmById(Long id);

    void clearFilms();
}
