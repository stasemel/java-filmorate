package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private Long id;

    @Email(message = "Некорректный email")
    @NotEmpty(message = "Email должен быть указан")
    private String email;

    @NotEmpty(message = "Логин должен быть указан")
    private String login;

    private String name;

    @PastOrPresent(message = "Дата рождения не может быть больше текущей даты")
    private LocalDate birthday;

    private final Set<Long> likedFilms = new HashSet<>();

    private final Set<Long> friends = new HashSet<>();

    public boolean validate() {
        if ((getEmail() == null) || (getEmail().isBlank())) {
            throw new ValidationException("Email не должен быть пустым");
        }
        if (!getEmail().contains("@")) {
            throw new ValidationException("Некорректный email");
        }
        if ((getBirthday() != null) && (getBirthday().isAfter(LocalDate.now()))) {
            throw new ValidationException("Дата рождения не может быть больше текущей даты");
        }
        if ((getLogin() == null) || (getLogin().contains(" "))) {
            throw new ValidationException("Логин не может быть пустым или содержать пробелы");
        }
        return true;
    }

    public User cloneUser() {
        User user = new User();
        user.setLogin(getLogin());
        user.setId(getId());
        user.setBirthday(getBirthday());
        user.setEmail(getEmail());
        user.setName(getName());
        user.friends.addAll(friends);
        user.likedFilms.addAll(likedFilms);
        return user;
    }
}
