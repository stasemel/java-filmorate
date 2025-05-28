package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class FilmControllerTest {
    FilmController controller = new FilmController();
    Film film = new Film();

    @BeforeEach
    void setUp() {
        controller.getService().getStorage().clearFilms();
        film.setName("Film 1");
        film.setDescription("Описание 1");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.now());
    }

    Film createNewFilm(int suffix) {
        Film film = new Film();
        film.setName(String.format("Film %d", suffix));
        film.setDescription(String.format("Описание %d", suffix));
        film.setDuration(100 + suffix);
        film.setReleaseDate(LocalDate.now().minusYears(suffix));
        return film;
    }

    @Test
    void testCreate() {
        Film createdFilm = controller.create(film);
        assertEquals(createdFilm, film, "Не совпадают фильмы");
        assertEquals(1, controller.findAll().size(), "Не добавился фильм в коллекцию");
    }

    @Test
    void testCreateEmptyName() {
        film.setName(null);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> controller.create(film),
                "Не отработала проверка пустого названия фильма");
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode(),
                "Некорректный код статуса проверки пустого названия фильма");
        assertEquals("Название фильма должно быть указано", exception.getReason(),
                "Некорректная причина ошибки проверки пустого имени");
    }

    @Test
    void testCreateDiplicateName() {
        controller.create(createNewFilm(123));
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> controller.create(createNewFilm(123)),
                "Не отработала проверка повтора регистрации фильма");
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode(),
                "Некорректный код статуса проверки повтора регистрации фильма");
        assertEquals("Уже есть фильм 'Film 123' в коллекции c id = 1", exception.getReason(),
                "Некорректная причина ошибки проверки повтора регистрации фильма");
    }

    @Test
    void testCreateWrongReleaseDate() {
        film.setReleaseDate(Film.MIN_RELEASE_DATE.minusDays(1));
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> controller.create(film),
                "Не отработала проверка неправильной даты");
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode(),
                "Некорректный код статуса проверки неправильной даты");
        assertEquals("Дата релиза не может быть раньше 1895-12-28", exception.getReason(),
                "Некорректная причина ошибки проверки неправильной даты");
    }

    @Test
    void testCreateWrongDuration() {
        film.setDuration(-100);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> controller.create(film),
                "Не отработала проверка неправильной даты");
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode(),
                "Некорректный код статуса проверки неправильной даты");
        assertEquals("Продолжительность фильма должна быть положительным числом", exception.getReason(),
                "Некорректная причина ошибки проверки неправильной даты");
    }

    @Test
    void testCreateWrongDescription() {
        String description201 = new String(new char[201]).replace('\0', 'x');
        film.setDescription(description201);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> controller.create(film),
                "Не отработала проверка описания");
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode(),
                "Некорректный код статуса проверки описания");
        assertEquals("Описание не должно превышать 200 символов", exception.getReason(),
                "Некорректная причина ошибки проверки описания");
    }

    @Test
    void testFindAll() {
        controller.create(film);
        controller.create(createNewFilm(1));
        controller.create(createNewFilm(2));
        assertEquals(controller.findAll().size(), controller.findAll().size(), "Не совпадает количество фильмов");
    }

    @Test
    void testUpdate() {
        Film createdFilm = controller.create(film);
        Film updateFilm = new Film();
        updateFilm.setId(createdFilm.getId());
        updateFilm.setName("Новое имя");
        Film changedFilm = controller.update(updateFilm);
        assertTrue(controller.getService().getStorage().getFilmById(changedFilm.getId()).isPresent(), "Не обнаружен фильм");
        assertEquals("Новое имя", controller.getService().getStorage().getFilmById(changedFilm.getId()).get().getName(),
                "Не изменилось имя при обновлении");
    }

    @Test
    void testUpdateEmptyData() {
        Film createdFilm = controller.create(film);
        Film updateFilm = new Film();
        updateFilm.setId(createdFilm.getId());
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> controller.update(updateFilm),
                "Не отработала проверка изменения с пустыми данными");
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode(),
                "Некорректный код статуса изменения с пустыми данными");
        assertEquals("Нет данных для изменения", exception.getReason(),
                "Некорректная причина изменения с пустыми данными");
    }

    @Test
    void testUpdateWrongName() {
        Film createdFilm = controller.create(film);
        Film updateFilm = new Film();
        updateFilm.setId(createdFilm.getId());
        updateFilm.setName(" ");
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> controller.update(updateFilm),
                "Не отработала проверка изменения с пустыми данными");
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode(),
                "Некорректный код статуса изменения с пустыми данными");
        assertEquals("Название фильма должно быть указано", exception.getReason(),
                "Некорректная причина изменения с пустыми данными");
    }

    @Test
    void testUpdateWrongReleaseDate() {
        Film createdFilm = controller.create(film);
        Film updateFilm = new Film();
        updateFilm.setId(createdFilm.getId());
        updateFilm.setReleaseDate(Film.MIN_RELEASE_DATE.minusDays(1));
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> controller.update(updateFilm),
                "Не отработала проверка изменения с некорректной датой релиза");
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode(),
                "Некорректный код статуса изменения с некорректной датой релиза");
        assertEquals("Дата релиза не может быть раньше 1895-12-28", exception.getReason(),
                "Некорректная причина изменения с некорректной датой релиза");
    }

    @Test
    void testUpdateDuration() {
        Film createdFilm = controller.create(film);
        Film updateFilm = new Film();
        updateFilm.setId(createdFilm.getId());
        updateFilm.setDuration(-100);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> controller.update(updateFilm),
                "Не отработала проверка изменения с некорректной датой релиза");
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode(),
                "Некорректный код статуса изменения с некорректной датой релиза");
        assertEquals("Продолжительность фильма должна быть положительным числом", exception.getReason(),
                "Некорректная причина изменения с некорректной датой релиза");
    }

    @Test
    void testUpdateDescription() {
        String description201 = new String(new char[201]).replace('\0', 'x');
        Film createdFilm = controller.create(film);
        Film updateFilm = new Film();
        updateFilm.setId(createdFilm.getId());
        updateFilm.setDescription(description201);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> controller.update(updateFilm),
                "Не отработала проверка изменения с некорректной датой релиза");
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode(),
                "Некорректный код статуса изменения с некорректной датой релиза");
        assertEquals("Описание не должно превышать 200 символов", exception.getReason(),
                "Некорректная причина изменения с некорректной датой релиза");
    }

    @Test
    void testUpdateDuplicate() {
        Film createdFilm = controller.create(film);
        controller.create(createNewFilm(123));
        Film updateFilm = createNewFilm(123);
        updateFilm.setId(createdFilm.getId());
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> controller.update(updateFilm),
                "Не отработала проверка изменения с некорректной датой релиза");
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode(),
                "Некорректный код статуса изменения с некорректной датой релиза");
        assertEquals("Уже есть фильм 'Film 123' в коллекции c id = 2", exception.getReason(),
                "Некорректная причина изменения с некорректной датой релиза");
    }

    @Test
    void testUpdateWrongId() {
        Film updateFilm = createNewFilm(123);
        updateFilm.setId(123L);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> controller.update(updateFilm),
                "Не отработала проверка изменения с некорректным id");
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode(),
                "Некорректный код статуса изменения с некорректной датой релиза");
        assertEquals("Не найден фильм с Id 123", exception.getReason(),
                "Некорректная причина изменения с некорректной датой релиза");
    }
}