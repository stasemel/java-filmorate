package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    UserController controller = new UserController(new UserService(new InMemoryUserStorage()));
    User user = new User();

    @BeforeEach
    void setUp() {
        controller.getService().getStorage().clearUsers();
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
        assertEquals(1, controller.findAll().size(), "Не добавился пользователь");
    }

    @Test
    void testCreateEmptyLogin() {
        user.setLogin(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.create(user),
                "Не отработала проверка пустого логина");
        assertEquals("Логин не может быть пустым или содержать пробелы", exception.getMessage(),
                "Некорректная причина ошибки проверки пустого логина");
    }

    @Test
    void testCreateEmptyEmail() {
        user.setEmail(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.create(user),
                "Не отработала проверка пустого email");
        assertEquals("Email не должен быть пустым", exception.getMessage(),
                "Некорректная причина ошибки проверки пустого email");
    }

    @Test
    void testCreateWrongEmail() {
        user.setEmail("a.ru");
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.create(user),
                "Не отработала проверка неправильного email");
        assertEquals("Некорректный email", exception.getMessage(),
                "Некорректная причина ошибки проверки неправильного email");
    }

    @Test
    void testCreateDuplicateEmail() {
        controller.create(user);
        controller.create(createNewUser(10));
        User duplicateLoginUser = createNewUser(10);
        duplicateLoginUser.setLogin("login1111");
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.create(duplicateLoginUser),
                "Не отработала проверка повтора email");
        assertEquals("Пользователь с email mail10@yandex.ru уже существует", exception.getMessage(),
                "Некорректная причина ошибки проверки повтора email");
    }

    @Test
    void testCreateDuplicateLogin() {
        controller.create(user);
        controller.create(createNewUser(10));
        User duplicateLoginUser = createNewUser(10);
        duplicateLoginUser.setEmail("login1111@yandex.ru");
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.create(duplicateLoginUser),
                "Не отработала проверка повтора email");
        assertEquals("Пользователь с логином user10 уже существует", exception.getMessage(),
                "Некорректная причина ошибки проверки повтора login");
    }

    @Test
    void testCreateWrongBirthday() {
        user.setBirthday(LocalDate.now().plusDays(1));
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.create(user),
                "Не отработала проверка неправильной даты рождения");
        assertEquals("Дата рождения не может быть больше текущей даты", exception.getMessage(),
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
        assertTrue(controller.getService().getStorage().getUserById(updateUser.getId()).isPresent(), "Не обнаружен пользователь");
        assertEquals("Новое имя", controller.getService().getStorage().getUserById(updateUser.getId()).get().getName(),
                "Не изменилось имя при обновлении");
        assertEquals("Новое имя", updatedUser.getName(),
                "Не изменилось имя при обновлении");
    }

    @Test
    void testUpdateEmptyData() {
        User createdUser = controller.create(user);
        User updateUser = new User();
        updateUser.setId(createdUser.getId());
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.update(updateUser),
                "Не отработала проверка обновления с пустыми данными");
        assertEquals("Нет данных для изменения", exception.getMessage(),
                "Некорректная причина ошибки проверки обновления с пустыми данными");

    }

    @Test
    void testUpdateWrongEmail() {
        User createdUser = controller.create(user);
        User updateUser = new User();
        updateUser.setId(createdUser.getId());
        updateUser.setEmail("a.ru");
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.update(updateUser),
                "Не отработала проверка обновления с неправильным email");
        assertEquals("Некорректный email", exception.getMessage(),
                "Некорректная причина ошибки проверки обновления с неправильным email");

    }

    @Test
    void testUpdateWrongBirthday() {
        User createdUser = controller.create(user);
        User updateUser = new User();
        updateUser.setId(createdUser.getId());
        updateUser.setBirthday(LocalDate.now().plusDays(1));
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.update(updateUser),
                "Не отработала проверка обновления с неправильным email");
        assertEquals("Дата рождения не может быть больше текущей даты", exception.getMessage(),
                "Некорректная причина ошибки проверки обновления с неправильным email");

    }

    @Test
    void testUpdateDuplicateEmail() {
        User createdUser = controller.create(user);
        User createUser2 = controller.create(createNewUser(10));
        User updateUser = new User();
        updateUser.setId(createdUser.getId());
        updateUser.setEmail(createUser2.getEmail());
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.update(updateUser),
                "Не отработала проверка повтора email");
        assertEquals("Пользователь с email mail10@yandex.ru уже существует", exception.getMessage(),
                "Некорректная причина ошибки проверки повтора email");
    }

    @Test
    void testUpdateDuplicateLogin() {
        User createdUser = controller.create(user);
        User createUser2 = controller.create(createNewUser(10));
        User updateUser = new User();
        updateUser.setId(createdUser.getId());
        updateUser.setLogin(createUser2.getLogin());
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.update(updateUser),
                "Не отработала проверка повтора логина");
        assertEquals("Пользователь с логином user10 уже существует", exception.getMessage(),
                "Некорректная причина ошибки проверки повтора логина");
    }

}