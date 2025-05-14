package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Data
@RestController
@RequestMapping("/films")
public class FilmController {
    private Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.debug("Receive new film: {}", film);
        try {
            if (film.validate()) {
                log.trace("Validate success");
            }
            if (isNotDuplicate(film)) {
                log.trace("Is Not duplicated");
            }
        } catch (RuntimeException e) {
            log.debug("POST error: {}, user: {}", e, film);
            log.warn("POST error: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        // сохраняем нового пользователя
        addNewFilm(film);
        log.info("Create film: {}", film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.debug("Receive update user {}", film);
        if (film.getId() == null) {
            log.warn("PUT error: empty id");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id должен быть указан");
        }
        if (!films.containsKey(film.getId())) {
            log.warn("PUT error: film not found by id {}", film.getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найден пользователь с таким Id");
        }
        Film cloneFilm = films.get(film.getId()).cloneFilm();
        if (film.getName() != null) cloneFilm.setName(film.getName());
        if (film.getDescription() != null) cloneFilm.setDescription(film.getDescription());
        if (film.getDuration() != null) cloneFilm.setDuration(film.getDuration());
        if (film.getReleaseDate() != null) cloneFilm.setReleaseDate(film.getReleaseDate());

        try {
            if (cloneFilm.validate()) {
                log.trace("Validate success");
            }
            if (isNotDuplicate(cloneFilm)) {
                log.trace("Is not duplicated");
            }
        } catch (RuntimeException e) {
            log.warn("PUT error: {}", e.getMessage());
            log.debug("PUT error: {}, user: {}", e, film);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        saveFilm(cloneFilm);
        log.info("Update user: {}", cloneFilm);
        return cloneFilm;
    }

    private void addNewFilm(Film film) {
        film.setId(getNextId());
        saveFilm(film);
    }

    private void saveFilm(Film film) {
        films.put(film.getId(), film);
    }

    private boolean isNotDuplicate(Film film) {
        if ((films.values().stream().anyMatch(film1 -> film1.equals(film) && (film1.getId().equals(film.getId()))))) {
            throw new ValidationException("Уже есть такой фильм в коллекции");
        }
        return true;
    }

    private int getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
