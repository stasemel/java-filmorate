package ru.yandex.practicum.filmorate.storage.film;

import lombok.ToString;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository("memoryFilmStorage")
@ToString
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Optional<Film> createFilm(Film film) {
        Long id = getNextId();
        film.setId(id);
        films.put(id, film);
        return Optional.of(film);
    }

    @Override
    public Optional<Film> updateFilm(Film film) {
        if (film.getId() == null) return Optional.empty();
        films.put(film.getId(), film);
        return Optional.of(film);
    }


    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        if (films.containsKey(id)) {
            return Optional.of(films.get(id).cloneFilm());
        }
        return Optional.empty();
    }

    @Override
    public void clearFilms() {
        films.clear();
    }

    @Override
    public void likeFilmByUser(Long userId, Long filmId) {
        if (!films.containsKey(filmId)) {
            throw new NotFoundException(String.format("Не найден фильм с id %d", filmId));
        }
        films.get(filmId).getLikes().add(userId);
    }

    @Override
    public void deleteLikeFilmByUser(Long userId, Long filmId) {
        if (!films.containsKey(filmId)) {
            throw new NotFoundException(String.format("Не найден фильм с id %d", filmId));
        }
        films.get(filmId).getLikes().remove(userId);
    }

    @Override
    public List<Film> getMostPopular(int count) {
        if (count == 0) count = 10;
        Comparator<Film> likeComparator = (f1, f2) -> f2.getLikes().size() - f1.getLikes().size();
        return films.values().stream().sorted(likeComparator).map(Film::cloneFilm).limit(count).collect(Collectors.toList());
    }

    @Override
    public List<Genre> getGenresByFilmId(Long filmId) {
        Optional<Film> film = getFilmById(filmId);
        if (film.isEmpty()) return new ArrayList<>();
        return film.get().getGenres().stream().toList();
    }

    @Override
    public Optional<Film> getFilmByIdAllInfo(Long filmId) {
        return getFilmById(filmId);
    }

    @Override
    public boolean isNotDuplicate(Film film) {
        List<Film> duplicatedFilms = films.values().stream()
                .filter(film1 ->
                        film1.equals(film) && ((film.getId() == null) || (!film1.getId().equals(film.getId()))))
                .toList();
        if (!duplicatedFilms.isEmpty()) {
            throw new ValidationException(String.format("Уже есть фильм '%s' в коллекции c id = %d",
                    duplicatedFilms.getFirst().getName(),
                    duplicatedFilms.getFirst().getId()));
        }
        return true;
    }

    private Long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
