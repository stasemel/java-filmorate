package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.SaveException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Getter
@Slf4j
public class FilmService {

    private final FilmStorage storage;
    private final UserService userService;
    private final RatingService ratingService;
    private final GenreService genreService;
    public static final boolean CHECK_DUPICATE = false;

    @Autowired
    public FilmService(@Qualifier("dbFilmStorage") FilmStorage storage, UserService service, RatingService ratingService, GenreService genreService) {
        this.storage = storage;
        this.userService = service;
        this.ratingService = ratingService;
        this.genreService = genreService;
    }

    public Film createFilm(Film film) {
        try {
            if (validate(film)) {
                log.trace("Validate success");
            }
            if (isNotDuplicate(film)) {
                log.trace("Is Not duplicated");
            }
        } catch (ValidationException e) {
            log.debug("Create film validation error: {}, user: {}", e, film);
            log.warn("Create film validation error: {}", e.getMessage());
            throw new ValidationException(e.getMessage());
        } catch (NotFoundException e) {
            log.warn("Create film not found error {}", e.getMessage());
            throw new NotFoundException(e.getMessage());
        } catch (RuntimeException e) {
            log.warn("Create film runtime error {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        // сохраняем нового пользователя
        Optional<Film> optionalFilm = storage.createFilm(film);
        if (optionalFilm.isEmpty()) {
            log.warn("Ошибка при создании фильма {}", film);
            throw new SaveException("Ошибка при создании фильма");
        }
        log.info("Create film: {}", film);
        return film;
    }

    public Collection<Film> findAll() {
        return storage.findAll();
    }

    public Film updateFilm(Film film) {
        if (film.getId() == null) {
            log.warn("Update error: empty id");
            throw new ValidationException("Id должен быть указан");
        }
        Optional<Film> optionalFilm = storage.getFilmById(film.getId());
        if (optionalFilm.isEmpty()) {
            log.warn("Update error: film not found by id {}", film.getId());
            throw new NotFoundException(String.format("Не найден фильм с Id %d", film.getId()));
        }
        if ((film.getName() == null) && (film.getDuration() == null) && (film.getDescription() == null)
                && (film.getReleaseDate() == null)) {
            log.warn("Update error: no data to change {}", film);
            throw new ValidationException("Нет данных для изменения");
        }
        Film cloneFilm = optionalFilm.get();
        if (film.getName() != null) cloneFilm.setName(film.getName());
        if (film.getDescription() != null) cloneFilm.setDescription(film.getDescription());
        if (film.getDuration() != null) cloneFilm.setDuration(film.getDuration());
        if (film.getReleaseDate() != null) cloneFilm.setReleaseDate(film.getReleaseDate());

        try {
            if (validate(cloneFilm)) {
                log.trace("Validate success");
            }
            if (isNotDuplicate(cloneFilm)) {
                log.trace("Is not duplicated");
            }
        } catch (NotFoundException e) {
            log.warn("Update film not found error {}", e.getMessage());
            throw new NotFoundException(e.getMessage());
        } catch (RuntimeException e) {
            log.warn("Update validation error: {}", e.getMessage());
            log.debug("Update validation error: {}, user: {}", e, film);
            throw new ValidationException(e.getMessage());
        }
        Optional<Film> optionalSavedFilm = storage.updateFilm(cloneFilm);
        if (optionalSavedFilm.isEmpty()) {
            log.warn("Не удалось сохранить изменения фильма {}", film);
            throw new SaveException("Не удалось сохранить изменения фильма");
        }
        log.info("Update user: {}", cloneFilm);
        return optionalSavedFilm.get();
    }

    public void likeFilmByUser(Long userId, Long filmId) {
        Optional<Film> optionalFilm = storage.getFilmById(filmId);
        if (optionalFilm.isEmpty()) {
            throw new NotFoundException(String.format("Не найден фильм с id %d", filmId));
        }
        Optional<User> optionalUser = userService.getStorage().getUserById(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException(String.format("Не найден пользователь с id %d", userId));
        }
        storage.likeFilmByUser(userId, filmId);
        userService.getStorage().likeFilmByUser(userId, filmId);
    }

    public void deleteLikeFilmByUser(Long userId, Long filmId) {
        Optional<Film> optionalFilm = storage.getFilmById(filmId);
        if (optionalFilm.isEmpty()) {
            throw new NotFoundException(String.format("Не найден фильм с id %d", filmId));
        }
        Optional<User> optionalUser = userService.getStorage().getUserById(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException(String.format("Не найден пользователь с id %d", userId));
        }
        storage.deleteLikeFilmByUser(userId, filmId);
        userService.getStorage().deleteLikeFilmByUser(userId, filmId);
    }

    public List<Film> getMostPopular(int count) {
        if (count == 0) count = 10;
        return storage.getMostPopular(count);
    }

    private boolean validate(Film film) {
        Rating mpa = film.getMpa();
        if (mpa != null) {
            if (ratingService.getRatingById(mpa.getId()) == null) {
                throw new NotFoundException(String.format("Не найден рейтинг с id = %d", mpa.getId()));
            }
        }
        if (film.getGenres() != null) {
            List<Genre> genreList = film.getGenres().stream().filter(genre -> {
                int id = genre.getId();
                try {
                    return genreService.getGenreById(id) == null;
                } catch (RuntimeException e) {
                    return true;
                }
            }).toList();
            if (!genreList.isEmpty()) {
                throw new NotFoundException(String.format("Не найдены жанры: %s", genreList));
            }
        }
        return film.validate();
    }

    private boolean isNotDuplicate(Film film) {
        if (!CHECK_DUPICATE) return true;
        return storage.isNotDuplicate(film);
    }

    public Film getFilmById(Long filmId) {
        Optional<Film> film = storage.getFilmByIdAllInfo(filmId);
        if (film.isEmpty()) {
            throw new NotFoundException(String.format("Не найден фильм с id =%d", filmId));
        }
        return film.get();
    }

    public List<Genre> getGenresByFilmId(Long filmId) {
        Optional<Film> film = storage.getFilmById(filmId);
        if (film.isEmpty()) {
            throw new NotFoundException(String.format("Не найден фильм с id =%d", filmId));
        }
        return storage.getGenresByFilmId(filmId);
    }
}
