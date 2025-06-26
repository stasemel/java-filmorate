package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class FilmRepository extends BaseRepository {
    public FilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    public Film save(Film film) {
        String query = "INSERT INTO films (\"name\",\"description\",\"release_date\",\"duration\",\"rating_id\") VALUES (?,?,?,?,?)";
        Rating mpa = film.getMpa();
        Integer mpaId = null;
        if (mpa != null) mpaId = mpa.getId();

        Long id = insert(query,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                mpaId
        );
        film.setId(id);
        log.trace("Call SQL {} for film {}, return id = {}", query, film, id);
        saveGenreForFilm(film);
        return film;
    }

    private void saveGenreForFilm(Film film) {
        if (!film.getGenres().isEmpty()) {
            delete("DELETE FROM film_genres WHERE \"film_id\"=?", film.getId());
            log.trace("Delete all genres fro film {}", film);
            String genreQuery = "INSERT INTO film_genres (\"film_id\",\"genre_id\") VALUES (?,?)";
            for (Genre genre : film.getGenres()) {
                update(genreQuery, film.getId(), genre.getId());
                log.trace("Save genre {} for film {}", genre.getId(), film.getId());
            }
        }
    }

    public Film updateFilm(Film film) {
        String query = "UPDATE films SET \"name\" = ?, \"description\" = ?, \"release_date\" = ?, \"duration\" = ? WHERE \"id\" = ?";
        update(
                query,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getId()
        );
        log.trace("Call SQL {} for film {}", query, film);
        return film;
    }

    public boolean isNotDuplicate(Film film) {
        String name = film.getName();
        LocalDate releaseDate = film.getReleaseDate();
        Integer duration = film.getDuration();
        Long id = film.getId();
        String query = "SELECT * FROM films WHERE \"name\"=? AND \"release_date\"=? AND \"duration\"=?";
        if (id != null) {
            query += " AND NOT (\"id\" = ?)";
            List<Film> duplicatedFilms = findMany(query, name, releaseDate, duration, id);
            if (!duplicatedFilms.isEmpty()) {
                throw new ValidationException(String.format("Уже есть фильм '%s' в коллекции c id = %d",
                        name,
                        duplicatedFilms.getFirst().getId()));
            }
        } else {
            List<Film> duplicatedFilms = findMany(query, name, releaseDate, duration);
            if (!duplicatedFilms.isEmpty()) {
                throw new ValidationException(String.format("Уже есть фильм '%s' в коллекции c id = %d",
                        name,
                        duplicatedFilms.getFirst().getId()));
            }
        }
        return true;
    }

    public Collection<Film> findAll() {
        return findMany("SELECT * FROM films");
    }

    public Optional<Film> getFimById(Long id) {
        return findOne("SELECT * FROM films WHERE \"id\" =?", id);
    }

    public List<Film> getMostPopular(int count) {
        if (count == 0) count = 10;
        String query = """
                SELECT
                    f.*
                FROM
                    FILMS AS f
                        LEFT OUTER JOIN FILM_LIKES AS fs ON f."id" = fs."film_id"
                GROUP BY
                    f."id"
                ORDER BY count(fs."user_id") DESC
                LIMIT ?
                """;
        return findMany(query, count);
    }

    public void likeFilm(Long userId, Long filmId) {
        String query = "MERGE INTO film_likes key (\"film_id\",\"user_id\") VALUES(?,?)";
        update(query, filmId, userId);
        log.trace("Call SQL {} for userId {} and filmId {}.", query, userId, filmId);
    }

    public void deleteLike(Long userId, Long filmId) {
        String query = "DELETE FROM film_likes WHERE \"film_id\"=? AND \"user_id\" = ?";
        boolean isDeleted = delete(query, filmId, userId);
        log.trace("Call SQL {} for userId {} and filmId {}. Result: {}", query, userId, filmId, isDeleted);
    }

    public List<Genre> getGenresByFilmId(Long filmId) {
        List<Genre> genreList = new ArrayList<>();
        String query = "SELECT DISTINCT \"genre_id\" FROM film_genres WHERE \"film_id\" = ?";
        List<Integer> list = jdbc.queryForList(query, Integer.class, filmId);
        for (int id : list) {
            genreList.add(new Genre(id, null));
        }
        return genreList;
    }
}
