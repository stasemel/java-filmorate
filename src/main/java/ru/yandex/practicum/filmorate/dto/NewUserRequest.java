package ru.yandex.practicum.filmorate.dto;

import lombok.Data;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;

@Data
public class NewUserRequest {
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;

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
}