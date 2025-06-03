package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserServiceTest {
    UserService service = new UserService(new InMemoryUserStorage());
    User user = new User();

    @BeforeEach
    void setUp() {
        service.getStorage().clearUsers();
        user.setName("Имя пользователя");
        user.setEmail("mail@yandex.ru");
        user.setLogin("user");
        user.setBirthday(LocalDate.now().minusYears(25));
        service.createUser(user);
    }

    User createNewUser(int suffix) {
        User user = new User();
        user.setName(String.format("Имя пользователя %d", suffix));
        user.setEmail(String.format("mail%d@yandex.ru", suffix));
        user.setLogin(String.format("user%d", suffix));
        user.setBirthday(LocalDate.now().minusYears(25).minusDays(suffix));
        return user;
    }

    @Test
    public void testAddFriend() {
        User user2 = createNewUser(2);
        service.createUser(user2);
        service.addFriend(user.getId(), user2.getId());
        assertEquals(1, service.getFriends(user.getId()).size(), "Не добавился друг к пользователю 1");
        assertEquals(2, service.getFriends(user.getId()).getFirst().getId(), "Неправильно добавился друг к пользователю 1");
        assertEquals(1, service.getFriends(user2.getId()).size(), "Не добавился друг к пользователю 2");
    }

    @Test
    public void testDeleteFriend() {
        User user2 = createNewUser(2);
        service.createUser(user2);
        User user3 = createNewUser(3);
        service.createUser(user3);
        service.addFriend(user.getId(), user2.getId());
        service.addFriend(user.getId(), user3.getId());
        service.deleteFriend(user.getId(), user2.getId());
        assertEquals(1, service.getFriends(user.getId()).size(), "Не удалися друг у пользователя 1");
        assertEquals(3, service.getFriends(user.getId()).getFirst().getId(), "Неправильно добавился друг к пользователю 1");
    }

    @Test
    public void testCommonFriends() {
        User user2 = createNewUser(2);
        service.createUser(user2);
        User user3 = createNewUser(3);
        service.createUser(user3);
        User user4 = createNewUser(4);
        service.createUser(user4);
        User user5 = createNewUser(5);
        service.createUser(user5);
        service.addFriend(user.getId(), user2.getId());
        service.addFriend(user3.getId(), user2.getId());
        service.addFriend(user4.getId(), user2.getId());
        service.addFriend(user3.getId(), user4.getId());
        service.addFriend(user2.getId(), user5.getId());
        service.addFriend(user3.getId(), user5.getId());
        List<User> commonFriends = service.getCommonFriends(user.getId(), user3.getId());
        List<User> commonFriends2 = service.getCommonFriends(user2.getId(), user3.getId());
        assertEquals(1, commonFriends.size(), "Не определились общие друзья");
        assertEquals(2, commonFriends.getFirst().getId(), "Неправильно определились общие друзья");
        assertEquals(2, commonFriends2.size(), "Не определились общие друзья юзеров 2 и 3");
    }

}