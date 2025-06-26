package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

@Repository
@Slf4j
public class GenreRepository extends BaseRepository {
    public GenreRepository(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    public Optional<Genre> getGenreById(Integer id) {
        String query = "SELECT * FROM genres WHERE \"id\" = ?";
        return findOne(query, id);
    }

    public Collection<Genre> findAll() {
        String query = "SELECT * FROM genres ORDER BY \"id\"";
        return findMany(query);
    }
}
