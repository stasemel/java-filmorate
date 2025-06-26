package ru.yandex.practicum.filmorate.storage.film;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository("dbFilmStorage")
@RequiredArgsConstructor
@Getter
public class DbFilmStorage implements FilmStorage {
    private final FilmRepository filmRepository;

    @Override
    public Optional<Film> createFilm(Film film) {
        return Optional.of(filmRepository.save(film));
    }

    @Override
    public Optional<Film> updateFilm(Film film) {
        return Optional.of(filmRepository.updateFilm(film));
    }

    @Override
    public boolean isNotDuplicate(Film film) {
        return filmRepository.isNotDuplicate(film);
    }

    @Override
    public Collection<Film> findAll() {
        return filmRepository.findAll();
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        return filmRepository.getFimById(id);
    }

    @Override
    public void clearFilms() {

    }

    @Override
    public void likeFilmByUser(Long userId, Long filmId) {
        filmRepository.likeFilm(userId, filmId);
    }

    @Override
    public void deleteLikeFilmByUser(Long userId, Long filmId) {
        filmRepository.deleteLike(userId, filmId);
    }

    @Override
    public List<Film> getMostPopular(int count) {
        return filmRepository.getMostPopular(count);
    }
}
