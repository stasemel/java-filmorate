package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@Slf4j
@Data
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService service;

    @Autowired
    public FilmController(FilmService service) {
        this.service = service;
    }

    @GetMapping
    public Collection<Film> findAll() {
        return service.findAll();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.debug("Receive new film: {}", film);
        return service.createFilm(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.debug("Receive update film {}", film);
        return service.updateFilm(film);
    }

    @PutMapping(value = {"/{filmId}/like/{userId}"})
    public void likeFilmByUser(@PathVariable Long userId, @PathVariable Long filmId) {
        log.debug("add like user {} to film {}", userId, filmId);
        service.likeFilmByUser(userId, filmId);
    }

    @DeleteMapping(value = {"/{filmId}/like/{userId}"})
    public void deleteLikeFilmByUser(@PathVariable Long userId, @PathVariable Long filmId) {
        log.debug("delete like from user {} to film {}", userId, filmId);
        service.deleteLikeFilmByUser(userId, filmId);
    }

    @GetMapping(value = {"/popular"})
    public List<Film> getPopulare(@RequestParam int count) {
        log.debug("get popular {}", count);
        return service.getMostPopular(count);
    }

    @GetMapping(value = "/{filmId}")
    public Film getFilmById(@PathVariable Long filmId) {
        log.debug("get film by id {}", filmId);
        return service.getFilmById(filmId);
    }
}
