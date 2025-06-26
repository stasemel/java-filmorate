package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.RatingRowMapper;

@Slf4j
@Repository
public class RatingRepository extends BaseRepository{

    public RatingRepository(JdbcTemplate jdbc, RatingRowMapper mapper) {
        super(jdbc, mapper);
    }

}
