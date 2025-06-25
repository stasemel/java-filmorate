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
        String query = "SELECT * FROM users";
        log.trace("Will call SQL {}", query);
        return findMany(query);
    }

    public Optional<User> getUserById(Long id) {
        String query = "SELECT * FROM users WHERE \"id\" = ?";
        log.trace("Will call SQL {} for id {}", query, id);
        return findOne(query, id);
    }

    public boolean isNotDuplicate(User user) {
        String email = user.getEmail();
        String login = user.getLogin();
        String query = "SELECT * FROM users WHERE \"email\" = ?";
        if (findOne(query, email).isPresent()) {
            throw new ValidationException(String.format("Пользователь с email %s уже существует", email));
        }
        log.trace("Called SQL {} for user {}", query, user);
        String queryLogin = "SELECT * FROM users WHERE \"login\" = ?";
        if (findOne(queryLogin, login).isPresent()) {
            throw new ValidationException(String.format("Пользователь с логином %s уже существует", login));
        }
        log.trace("Called SQL {} for user {}", query, user);
        return true;
    }

    public User save(User user) {
        String query = "INSERT INTO users (\"name\",\"email\",\"login\",\"birthday\") VALUES (?,?,?,?)";
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
        String query = "UPDATE users SET \"username\" = ?, \"email\" = ?, \"login\" = ?, \"birthday\" = ? WHERE \"id\" = ?";
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
        String query = "INSERT INTO friendships (\"user_id\",\"friend_id\",\"confirm\") VALUES (?,?,?)";
        update(query, userId, friendId, false);
        log.trace("Call SQL {} for userId {} and friendId {}", query, userId, friendId);
    }

    public boolean isFriendshipExists(Long userId, Long friendId) {
        String query = "SELECT count(*) FROM friendships WHERE \"user_id\" = ? AND \"friend_id\" = ?";
        int count = countRows(query, userId, friendId);
        log.trace("Call SQL {} for userId {} and friendId {}", query, userId, friendId);
        return (count > 0);
    }

    public void updateFriend(Long userId, Long friendId, boolean confirm) {
        String query = "UPDATE friendships SET \"confirm\" =? WHERE \"user_id\" = ? AND \"friend_id\" = ?";
        update(query, userId, friendId, false);
        log.trace("Call SQL {} for userId {} and friendId {}", query, userId, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        String query = "DELETE FROM friendships WHERE \"user_id\" = ? AND \"friend_id\" = ?";
        boolean isDeleted = delete(query, userId, friendId);
        log.trace("Call SQL {} for userId {} and friendId {}. Result: {}", query, userId, friendId, isDeleted);
    }

    public List<User> getFriendIdByUser(Long userId) {
        String query = "SELECT * from users AS u INNER JOIN friendships AS f ON f.\"friend_id\" = u.\"id\" WHERE f.\"user_id\" = ?";
        return findMany(query, userId);
    }

    public List<User> getCommonFriendIdByUser(Long userId, Long otherUserId) {
        String query = """
                SELECT DISTINCT *
                FROM users AS u
                WHERE u."id" IN
                      (SELECT "friend_id"
                       FROM friendships
                       WHERE ("user_id" = ?)
                         AND "friend_id" IN (
                           SELECT "friend_id" FROM FRIENDSHIPS WHERE "user_id" = ?
                           ));
                """;
        return findMany(query, userId, otherUserId);
    }
}
