package ru.yandex.practicum.filmorate.storage.film;

import lombok.ToString;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
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
