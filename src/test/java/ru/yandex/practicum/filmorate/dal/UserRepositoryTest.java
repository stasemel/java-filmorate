package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserRepository.class, UserRowMapper.class})
class UserRepositoryTest {
    private final UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.delete("DELETE FROM friendships");
        userRepository.delete("DELETE FROM users");
        String queryUserCreate = """
                INSERT INTO users ("name", "email", "login","birthday")
                VALUES ('user1','user1@mail.ru','login1','2019-12-31'),
                       ('user2','user2@mail.ru','login2','2019-12-31'),
                       ('user3','user3@mail.ru','login3','2019-12-31'),
                       ('user4','user4@mail.ru','login4','2019-12-31');
                """;
        userRepository.update(queryUserCreate);
    }

    @Test
    void insertFriend() {
        List<User> list = (List<User>) userRepository.findAll();
        Long id1 = list.get(1).getId();
        Long id2 = list.get(2).getId();
        userRepository.insertFriend(id1, id2);
        boolean isFriendExists = userRepository.isFriendshipExists(id1, id2);
        assertTrue(isFriendExists, "Не создалась запись о дружбе");
    }

    @Test
    void isFriendshipExists() {
        userRepository.insertFriend(2L, 4L);
        boolean isFriendExists = userRepository.isFriendshipExists(2L, 4L);
        boolean isNotFriendExists = userRepository.isFriendshipExists(2L, 5L);
        assertTrue(isFriendExists, "Некорректно отвечает isFriendshipExists на true");
        assertFalse(isNotFriendExists, "Некорректно отвечает isFriendshipExists на false");
    }

}