package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest {
    User user = new User();

    @BeforeEach
    void setUp() {
        user.setName("Имя пользователя");
        user.setEmail("mail@yandex.ru");
        user.setLogin("user1");
        user.setBirthday(LocalDate.now().minusYears(25));
    }

    @Test
    void validateCorrectUser() {
        assertTrue(user.validate(), "Не прошла валидация корректного пользователя");
    }

    @Test
    void validateLogin() {
        user.setLogin(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> user.validate(),
                "Не отработала проверка пустого логина");
        assertEquals("Логин не может быть пустым или содержать пробелы", exception.getMessage(),
                "Не совпадает текст ошибки проверки пустого логина");
        user.setLogin(" ");
        ValidationException exception2 = assertThrows(ValidationException.class, () -> user.validate(),
                "Не отработала проверка пустого логина");
        assertEquals("Логин не может быть пустым или содержать пробелы", exception2.getMessage(),
                "Не совпадает текст ошибки проверки пустого логина");
    }

    @Test
    void validateBirthday() {
        user.setBirthday(LocalDate.now().plusDays(1));
        ValidationException exception = assertThrows(ValidationException.class, () -> user.validate(),
                "Не отработала проверка дня рождения");
        assertEquals("Дата рождения не может быть больше текущей даты", exception.getMessage(),
                "Не совпадает текст ошибки проверки дня рождения");
    }

    @Test
    void validateEmail() {
        user.setEmail(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> user.validate(),
                "Не отработала проверка пустого email");
        assertEquals("Email не должен быть пустым", exception.getMessage(),
                "Не совпадает текст ошибки проверки пустого email");
        user.setEmail(" ");
        ValidationException exception2 = assertThrows(ValidationException.class, () -> user.validate(),
                "Не отработала проверка пустого email");
        assertEquals("Email не должен быть пустым", exception2.getMessage(),
                "Не совпадает текст ошибки проверки пустого email");
        user.setEmail("a.ru");
        ValidationException exception3 = assertThrows(ValidationException.class, () -> user.validate(),
                "Не отработала проверка некорректного email");
        assertEquals("Некорректный email", exception3.getMessage(),
                "Не совпадает текст ошибки проверки некорректного email");
    }
}