package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getTimestamp("release_date").toLocalDateTime().toLocalDate());
        film.setDuration(rs.getInt("duration"));
        int mpaId = rs.getInt("rating_id");
        if (mpaId != 0) {
            Rating mpa = new Rating();
            mpa.setId(mpaId);
            film.setMpa(mpa);
        }
        return film;
    }
}
