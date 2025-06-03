package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Optional<Film> createFilm(Film film);

    Optional<Film> updateFilm(Film film);

    boolean isNotDuplicate(Film film);

    Collection<Film> findAll();

    Optional<Film> getFilmById(Long id);

    void clearFilms();

    void likeFilmByUser(Long userId, Long filmId);

    void deleteLikeFilmByUser(Long userId, Long filmId);

    List<Film> getMostPopular(int count);
}
