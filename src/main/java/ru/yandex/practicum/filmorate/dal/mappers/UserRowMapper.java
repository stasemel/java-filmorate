package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class UserRowMapper implements RowMapper<User> {


    @Override
    public User mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        User user = new User();
        user.setId(resultSet.getLong("id"));
        user.setEmail(resultSet.getString("email"));
        user.setLogin(resultSet.getString("login"));
        user.setName(resultSet.getString("name"));
        user.setBirthday(resultSet.getTimestamp("birthday").toLocalDateTime().toLocalDate());
        return user;
    }
}
