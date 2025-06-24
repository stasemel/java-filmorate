package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

@Repository
public class UserRepository extends BaseRepository<User> {

    public UserRepository(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    public Collection<User> findAll() {
        String query = "SELECT * FROM users";
        return findMany(query);
    }

    public boolean isNotDuplicate(User user) {
        String email = user.getEmail();
        String login = user.getLogin();
        String query = "SELECT * FROM users WHERE email = ?";
        if (findOne(query, email).isPresent()) {
            throw new ValidationException(String.format("Пользователь с email %s уже существует", email));
        }
        String queryLogin = "SELECT * FROM users WHERE login = ?";
        if (findOne(queryLogin, login).isPresent()) {
            throw new ValidationException(String.format("Пользователь с логином %s уже существует", login));
        }
        return true;
    }

    public User save(User user) {
        Long id = insert("INSERT INTO users (name,email,login,birthday) VALUES (?,?,?,?) returning id",
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday()
        );
        user.setId(id);
        return user;
    }
}
