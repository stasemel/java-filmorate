package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Repository
public class RatingRepository extends BaseRepository {

    public RatingRepository(JdbcTemplate jdbc, RowMapper<Rating> mapper) {
        super(jdbc, mapper);
    }

    public Optional<Rating> getRaingsById(Integer id) {
        String query = "SELECT * FROM RATINGS WHERE \"id\" = ?";
        return findOne(query, id);
    }

    public Collection<Rating> getAll() {
        String query = "SELECT * FROM ratings ORDER BY \"id\"";
        return findMany(query);
    }
}
