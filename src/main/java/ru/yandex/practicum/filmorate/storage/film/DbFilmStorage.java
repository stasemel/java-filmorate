package ru.yandex.practicum.filmorate.storage.film;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.RatingService;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository("dbFilmStorage")
@RequiredArgsConstructor
@Getter
public class DbFilmStorage implements FilmStorage {
    private final FilmRepository filmRepository;
    private final RatingService ratingService;
    private final GenreService genreService;

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

    @Override
    public List<Genre> getGenresByFilmId(Long filmId) {
        return filmRepository.getGenresByFilmId(filmId);
    }

    @Override
    public Optional<Film> getFilmByIdAllInfo(Long filmId) {
        Optional<Film> optionalFilm = getFilmById(filmId);
        if (optionalFilm.isEmpty()) return Optional.empty();
        Film film = optionalFilm.get();
        if (film.getMpa() != null) {
            Rating rating = ratingService.getRatingById(film.getMpa().getId());
            film.setMpa(rating);
        }
        List<Genre> genreSet = getGenresByFilmId(filmId);
        for (Genre genre : genreSet) {
            Genre fullInfoGenre = genreService.getGenreById(genre.getId());
            if (fullInfoGenre != null) {
                film.getGenres().add(fullInfoGenre);
            }
        }
        return Optional.of(film);
    }
}
