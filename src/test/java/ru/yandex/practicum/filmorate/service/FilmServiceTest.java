package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.InMemoryGenreStorage;
import ru.yandex.practicum.filmorate.storage.rating.InMemoryRatingStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FilmServiceTest {
    UserService userService = new UserService(new InMemoryUserStorage());
    RatingService ratingService = new RatingService(new InMemoryRatingStorage());
    GenreService genreService = new GenreService(new InMemoryGenreStorage());
    FilmService service = new FilmService(
            new InMemoryFilmStorage(), userService, ratingService, genreService);

    Film film = new Film();
    User user = new User();

    @BeforeEach
    void setUp() {
        service.getStorage().clearFilms();
        userService.getStorage().clearUsers();
        film.setName("Film 1");
        film.setDescription("Описание 1");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.now());
        film = service.createFilm(film);
        user.setName("Имя пользователя");
        user.setEmail("mail@yandex.ru");
        user.setLogin("user");
        user.setBirthday(LocalDate.now().minusYears(25));
        user = userService.createUser(user);
    }

    Film createNewFilm(int suffix) {
        Film film = new Film();
        film.setName(String.format("Film %d", suffix));
        film.setDescription(String.format("Описание %d", suffix));
        film.setDuration(100 + suffix);
        film.setReleaseDate(LocalDate.now().minusYears(suffix));
        return film;
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
    public void testAddLikes() {
        User user1 = createNewUser(1);
        userService.createUser(user1);
        service.likeFilmByUser(user.getId(), film.getId());
        service.likeFilmByUser(user1.getId(), film.getId());
        assertEquals(2, film.getLikes().size(), "Не добавился like к фильму");
    }

    @Test
    public void testAddLikesDouble() {
        User user1 = createNewUser(1);
        userService.createUser(user1);
        service.likeFilmByUser(user.getId(), film.getId());
        service.likeFilmByUser(user1.getId(), film.getId());
        service.likeFilmByUser(user.getId(), film.getId());
        assertEquals(2, film.getLikes().size(), "Повторно добавился like к фильму");
    }

    @Test
    public void testDeleteLikes() {
        User user1 = createNewUser(1);
        userService.createUser(user1);
        service.likeFilmByUser(user.getId(), film.getId());
        service.likeFilmByUser(user1.getId(), film.getId());
        service.deleteLikeFilmByUser(user.getId(), film.getId());
        assertEquals(1, film.getLikes().size(), "Не удалился like к фильму");
    }

    @Test
    public void testGetMostPopular() {
        User user2 = createNewUser(2);
        User user3 = createNewUser(3);
        User user4 = createNewUser(4);
        Film film2 = createNewFilm(2);
        Film film3 = createNewFilm(3);
        Film film4 = createNewFilm(4);
        userService.createUser(user2);
        userService.createUser(user3);
        userService.createUser(user4);
        service.createFilm(film2);
        service.createFilm(film3);
        service.createFilm(film4);
        service.likeFilmByUser(user.getId(), film.getId());
        service.likeFilmByUser(user2.getId(), film.getId());
        service.likeFilmByUser(user3.getId(), film.getId());
        service.likeFilmByUser(user4.getId(), film.getId());
        service.likeFilmByUser(user2.getId(), film2.getId());
        service.likeFilmByUser(user3.getId(), film2.getId());
        service.likeFilmByUser(user4.getId(), film2.getId());
        service.likeFilmByUser(user3.getId(), film3.getId());
        service.likeFilmByUser(user4.getId(), film3.getId());
        service.likeFilmByUser(user4.getId(), film4.getId());
        List<Film> popular = service.getMostPopular(3);
        assertEquals(3, popular.size(), "Вернулось некорректнре количество фильмов");
        assertEquals(1, popular.getFirst().getId(), "Вернулась некорректная последовательность фильмов");
        assertEquals(2, popular.get(1).getId(), "Вернулась некорректная последовательность фильмов во второй позиции");
        assertEquals(3, popular.get(2).getId(), "Вернулась некорректная последовательность фильмов в третьей позиции");
    }
}