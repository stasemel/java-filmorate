package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserControllerTest {
    UserController controller = new UserController();
    User user = new User();

    @BeforeEach
    void setUp() {
        controller.getUsers().clear();
        controller.getEmails().clear();
        controller.getLogins().clear();
        user.setName("Имя пользователя");
        user.setEmail("mail@yandex.ru");
        user.setLogin("user");
        user.setBirthday(LocalDate.now().minusYears(25));
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
    void createUser() {
        User createdUser = controller.create(user);
        assertEquals(createdUser, user, "Не совпадают пользователи");
        assertEquals(1, controller.getUsers().size(), "Не добавился пользователь");
    }

    @Test
    void testCreateEmptyLogin() {
        user.setLogin(null);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> controller.create(user),
                "Не отработала проверка пустого логина");
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode(),
                "Некорректный код статуса проверки пустого логина");
        assertEquals("Логин не может быть пустым или содержать пробелы", exception.getReason(),
                "Некорректная причина ошибки проверки пустого логина");
    }

    @Test
    void testCreateEmptyEmail() {
        user.setEmail(null);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> controller.create(user),
                "Не отработала проверка пустого email");
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode(),
                "Некорректный код статуса проверки пустого email");
        assertEquals("Email не должен быть пустым", exception.getReason(),
                "Некорректная причина ошибки проверки пустого email");
    }

    @Test
    void testCreateWrongEmail() {
        user.setEmail("a.ru");
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> controller.create(user),
                "Не отработала проверка неправильного email");
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode(),
                "Некорректный код статуса проверки неправильного email");
        assertEquals("Некорректный email", exception.getReason(),
                "Некорректная причина ошибки проверки неправильного email");
    }

    @Test
    void testCreateDuplicateEmail() {
        controller.create(user);
        controller.create(createNewUser(10));
        User duplicateLoginUser = createNewUser(10);
        duplicateLoginUser.setLogin("login1111");
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> controller.create(duplicateLoginUser),
                "Не отработала проверка повтора email");
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode(),
                "Некорректный код статуса проверки повтора email");
        assertEquals("Пользователь с email mail10@yandex.ru уже существует", exception.getReason(),
                "Некорректная причина ошибки проверки повтора email");
    }

    @Test
    void testCreateDuplicateLogin() {
        controller.create(user);
        controller.create(createNewUser(10));
        User duplicateLoginUser = createNewUser(10);
        duplicateLoginUser.setEmail("login1111@yandex.ru");
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> controller.create(duplicateLoginUser),
                "Не отработала проверка повтора email");
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode(),
                "Некорректный код статуса проверки повтора login");
        assertEquals("Пользователь с логином user10 уже существует", exception.getReason(),
                "Некорректная причина ошибки проверки повтора login");
    }

    @Test
    void testCreateWrongBirthday() {
        user.setBirthday(LocalDate.now().plusDays(1));
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> controller.create(user),
                "Не отработала проверка неправильной даты рождения");
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode(),
                "Некорректный код статуса проверки неправильной даты рождения");
        assertEquals("Дата рождения не может быть больше текущей даты", exception.getReason(),
                "Некорректная причина ошибки проверки неправильной даты рождения");
    }

    @Test
    void testFindAll() {
        controller.create(user);
        controller.create(createNewUser(1));
        controller.create(createNewUser(2));
        assertEquals(3, controller.findAll().size(), "Не совпадает количество пользователей");
    }

    @Test
    void testUpdate() {
        User createdUser = controller.create(user);
        User updateUser = new User();
        updateUser.setId(createdUser.getId());
        updateUser.setName("Новое имя");
        User updatedUser = controller.update(updateUser);
        assertEquals("Новое имя", controller.getUsers().get(updateUser.getId()).getName(),
                "Не изменилось имя при обновлении");
        assertEquals("Новое имя", updatedUser.getName(),
                "Не изменилось имя при обновлении");
    }

    @Test
    void testUpdateEmptyData() {
        User createdUser = controller.create(user);
        User updateUser = new User();
        updateUser.setId(createdUser.getId());
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> controller.update(updateUser),
                "Не отработала проверка обновления с пустыми данными");
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode(),
                "Некорректный код статуса проверки обновления с пустыми данными");
        assertEquals("Нет данных для изменения", exception.getReason(),
                "Некорректная причина ошибки проверки обновления с пустыми данными");

    }

    @Test
    void testUpdateWrongEmail() {
        User createdUser = controller.create(user);
        User updateUser = new User();
        updateUser.setId(createdUser.getId());
        updateUser.setEmail("a.ru");
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> controller.update(updateUser),
                "Не отработала проверка обновления с неправильным email");
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode(),
                "Некорректный код статуса проверки обновления с неправильным email");
        assertEquals("Некорректный email", exception.getReason(),
                "Некорректная причина ошибки проверки обновления с неправильным email");

    }

    @Test
    void testUpdateWrongBirthday() {
        User createdUser = controller.create(user);
        User updateUser = new User();
        updateUser.setId(createdUser.getId());
        updateUser.setBirthday(LocalDate.now().plusDays(1));
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> controller.update(updateUser),
                "Не отработала проверка обновления с неправильным email");
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode(),
                "Некорректный код статуса проверки обновления с неправильным email");
        assertEquals("Дата рождения не может быть больше текущей даты", exception.getReason(),
                "Некорректная причина ошибки проверки обновления с неправильным email");

    }

    @Test
    void testUpdateDuplicateEmail() {
        User createdUser = controller.create(user);
        User createUser2 = controller.create(createNewUser(10));
        User updateUser = new User();
        updateUser.setId(createdUser.getId());
        updateUser.setEmail(createUser2.getEmail());
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> controller.update(updateUser),
                "Не отработала проверка повтора email");
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode(),
                "Некорректный код статуса проверки повтора email");
        assertEquals("Пользователь с email mail10@yandex.ru уже существует", exception.getReason(),
                "Некорректная причина ошибки проверки повтора email");
    }

    @Test
    void testUpdateDuplicateLogin() {
        User createdUser = controller.create(user);
        User createUser2 = controller.create(createNewUser(10));
        User updateUser = new User();
        updateUser.setId(createdUser.getId());
        updateUser.setLogin(createUser2.getLogin());
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> controller.update(updateUser),
                "Не отработала проверка повтора логина");
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode(),
                "Некорректный код статуса проверки повтора логина");
        assertEquals("Пользователь с логином user10 уже существует", exception.getReason(),
                "Некорректная причина ошибки проверки повтора логина");
    }

}