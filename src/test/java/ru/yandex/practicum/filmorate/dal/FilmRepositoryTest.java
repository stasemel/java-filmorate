package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmRepository.class, FilmRowMapper.class})
class FilmRepositoryTest {
    private final FilmRepository filmRepository;
    private final Film film = new Film();

    @Test
    public void testCreateFilm() {
        film.setName("Film 1");
        film.setDescription("Описание 1");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.now());
        filmRepository.save(film);
        List<Film> list = (List<Film>) filmRepository.findAll();
        assertEquals(list.getLast().getId(), film.getId(), "Не создался фильм");
    }

    @Test
    public void testUpdateFilm() {
        film.setName("Film 1");
        film.setDescription("Описание 1");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.now());
        filmRepository.save(film);
        String newName = "Новое имя фильма";
        film.setName(newName);
        filmRepository.updateFilm(film);
        Optional<Film> filmFromBase = filmRepository.getFimById(film.getId());
        assertTrue(filmFromBase.isPresent(), "Не найден фильм в базе при изменении названия");
        assertEquals(newName, filmFromBase.get().getName(), "Не изменилось название фильма");
    }
}