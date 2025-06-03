package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class FilmTest {
    Film film = new Film();

    @BeforeEach
    void setUp() {
        film.setName("Film 1");
        film.setDescription("Описание 1");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.now());
    }

    @Test
    void testValidateName() {
        film.setName(null);
        ValidationException exception1 = assertThrows(ValidationException.class, () -> film.validate(),
                "Не отработала проверка наличия названия");
        assertEquals("Название фильма должно быть указано", exception1.getMessage(),
                "Не совпадает текст ошибки наличия названия");
        film.setName(" ");
        ValidationException exception2 = assertThrows(ValidationException.class, () -> film.validate(),
                "Не отработала проверка названия из пробелов");
        assertEquals("Название фильма должно быть указано", exception2.getMessage(),
                "Не совпадает текст ошибки проверки названия из пробелв");
    }

    @Test
    void testValidateReleaseDate() {
        LocalDate badDate = Film.MIN_RELEASE_DATE.minusDays(1);
        film.setReleaseDate(badDate);
        ValidationException exception3 = assertThrows(ValidationException.class, () -> film.validate(),
                "Не отработала проверка даты релиза");
        assertEquals("Дата релиза не может быть раньше 1895-12-28", exception3.getMessage(),
                "Не совпадает текст ошибки проверки  даты релиза");
    }

    @Test
    void testValidateDescription() {
        String description201 = new String(new char[201]).replace('\0', 'x');
        film.setDescription(description201);
        ValidationException exception4 = assertThrows(ValidationException.class, () -> film.validate(),
                "Не отработала проверка длины описания");
        assertEquals("Описание не должно превышать 200 символов", exception4.getMessage(),
                "Не совпадает текст ошибки проверки длины описания");
    }

    @Test
    void testValidateDuration() {
        film.setDuration(-100);
        ValidationException exception5 = assertThrows(ValidationException.class, () -> film.validate(),
                "Не отработала проверка продолжительности");
        assertEquals("Продолжительность фильма должна быть положительным числом", exception5.getMessage(),
                "Не совпадает текст ошибки проверки продолжительности");
    }
}