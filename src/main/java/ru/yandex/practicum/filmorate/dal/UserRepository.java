package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class UserRepository extends BaseRepository<User> {

    public UserRepository(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    public Collection<User> findAll() {
        String query = buildSQLSelect("users");
        log.trace("Will call SQL {}", query);
        return findMany(query);
    }

    public Optional<User> getUserById(Long id) {
        String query = buildSQLSelect("users", new String[]{"*"}, new String[]{"\"id\" = ?"}, new String[]{});
        log.trace("Will call SQL {} for id {}", query, id);
        return findOne(query, id);
    }

    public boolean isNotDuplicate(User user) {
        String email = user.getEmail();
        String login = user.getLogin();
        Long id = user.getId();
        int n = 1;
        if (id != null) {
            n++;
        }
        String[] wheres = new String[n];
        wheres[0] = "\"email\" = ?";
        if (id != null) {
            wheres[1] = "NOT (\"id\" = ?)";
        }
        String query = buildSQLSelect("users", new String[]{"*"}, wheres, new String[]{});
        Collection<User> usersWithEmail;
        if (id != null) {
            usersWithEmail = findMany(query, email, id);
        } else {
            usersWithEmail = findMany(query, email);
        }
        if (!usersWithEmail.isEmpty()) {
            throw new ValidationException(String.format("Пользователь с email %s уже существует", email));
        }
        log.trace("Called SQL {} for user {}", query, user);
        wheres[0] = "\"login\" = ?";
        String queryLogin = buildSQLSelect("users", new String[]{"*"}, wheres, new String[]{});
        Collection<User> usersWithLogin;
        if (id != null) {
            usersWithLogin = findMany(queryLogin, login, id);
        } else {
            usersWithLogin = findMany(queryLogin, login);
        }
        if (!usersWithLogin.isEmpty()) {
            throw new ValidationException(String.format("Пользователь с логином %s уже существует", login));
        }
        log.trace("Called SQL {} for user {}", queryLogin, user);
        return true;
    }

    public User save(User user) {
        String query = buildSQLInsert("users", new String[]{"\"name\"", "\"email\"", "\"login\"", "\"birthday\""});
        Long id = insert(query,
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday()
        );
        user.setId(id);
        log.trace("Call SQL {} for user {}, return id = {}", query, user, id);
        return user;
    }


    public User updateUser(User user) {
        String query = buildSQLUpdate("users",
                new String[]{"\"name\"=?", "\"email\"=?", "\"login\"=?", "\"birthday\" =?"},
                new String[]{"\"id\" = ?"});
        update(
                query,
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday(),
                user.getId()
        );
        log.trace("Call SQL {} for user {}", query, user);
        return user;
    }

    public void insertFriend(Long userId, Long friendId) {
        String query = buildSQLInsert("friendships",
                new String[]{"\"user_id\"", "\"friend_id\"", "\"confirm\""});
        update(query, userId, friendId, false);
        log.trace("Call SQL {} for userId {} and friendId {}", query, userId, friendId);
    }

    public boolean isFriendshipExists(Long userId, Long friendId) {
        String query = buildSQLSelect("friendships",
                new String[]{"count(*)"}, new String[]{"\"user_id\" = ?", "\"friend_id\" = ?"},
                new String[]{});
        int count = countRows(query, userId, friendId);
        log.trace("Call SQL {} for userId {} and friendId {}", query, userId, friendId);
        return (count > 0);
    }

    public void updateFriend(Long userId, Long friendId, boolean confirm) {
        String query = buildSQLUpdate("friendships",
                new String[]{"\"confirm\" =?"},
                new String[]{"\"user_id\" = ?", "\"friend_id\" = ?"});
        update(query, userId, friendId, false);
        log.trace("Call SQL {} for userId {} and friendId {}", query, userId, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        String query = buildSQLDelete("friendships", new String[]{"\"user_id\" = ?", "\"friend_id\" = ?"});
        boolean isDeleted = delete(query, userId, friendId);
        log.trace("Call SQL {} for userId {} and friendId {}. Result: {}", query, userId, friendId, isDeleted);
    }

    public List<User> getFriendIdByUser(Long userId) {
        String query = buildSQLSelect("users AS u INNER JOIN friendships AS f ON f.\"friend_id\" = u.\"id\"",
                new String[]{"*"},
                new String[]{"f.\"user_id\" = ?"},
                new String[]{});
        return findMany(query, userId);
    }

    public List<User> getCommonFriendIdByUser(Long userId, Long otherUserId) {
        String queryFriend = buildSQLSelect("friendships",
                new String[]{"\"friend_id\""},
                new String[]{"\"user_id\" = ?"},
                new String[]{});
        String queryUser = buildSQLSelect("friendships",
                new String[]{"\"friend_id\""},
                new String[]{"(\"user_id\" = ?)", String.format("\"friend_id\" IN (%s)", queryFriend)},
                new String[]{});
        String query = buildSQLSelect("users AS u",
                new String[]{"DISTINCT *"},
                new String[]{String.format("u.\"id\" IN (%s)", queryUser)},
                new String[]{});
        log.trace("Will call SQL {} for id {} and otherId {}", query, userId, otherUserId);
        return findMany(query, userId, otherUserId);
    }
}
