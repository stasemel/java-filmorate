package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.SaveException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.Optional;

@Service
@Getter
@Slf4j
public class FilmService {

    private final FilmStorage storage;

    @Autowired
    public FilmService(FilmStorage storage) {
        this.storage = storage;
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
        }catch (RuntimeException e){
            log.warn("Create film runtime error {}",e.getMessage());
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

    private boolean validate(Film film) {
        return film.validate();
    }

    private boolean isNotDuplicate(Film film) {
        return storage.isNotDuplicate(film);
    }


}
