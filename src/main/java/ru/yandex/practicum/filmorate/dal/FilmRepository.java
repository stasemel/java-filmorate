package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Optional;

@Slf4j
@Repository
public class FilmRepository extends BaseRepository {
    public FilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    public Film save(Film film) {
        String query = "INSERT INTO users (\"name\",\"description\",\"release_date\",\"duration\") VALUES (?,?,?,?)";
        Long id = insert(query,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration()
        );
        film.setId(id);
        log.trace("Call SQL {} for film {}, return id = {}", query, film, id);
        return film;
    }
}
