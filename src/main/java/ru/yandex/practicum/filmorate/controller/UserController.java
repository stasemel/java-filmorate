package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private final Map<String, User> emails = new HashMap<>();
    private final Map<String, User> logins = new HashMap<>();

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        try {
            user.validate();
            isNotDuplicate(user);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        if (emails.containsKey(user.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Пользоватеь с таким email уже существует");
        }
        user.setId(getNextId());
        // сохраняем нового пользователя в памяти приложения
        saveUser(user);
        return user;
    }

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    private void saveUser(User user) {
        users.put(user.getId(), user);
        emails.put(user.getEmail(), user);
        logins.put(user.getLogin(), user);
    }

    private Boolean isNotDuplicate(User user) {
        String email = user.getEmail();
        String login = user.getLogin();
        Integer id = user.getId();
        if (emails.containsKey(email) && (emails.get(email).getId() != id)) {
            throw new ValidationException("Пользователь с таким email уже существует");
        }
        if (logins.containsKey(login) && (logins.get(email).getId() != id)) {
            throw new ValidationException("Пользователь с таким логином уже существует");
        }
        return true;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {

        if (user.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id должен быть указан");
        }
        if (!users.containsKey(user.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найден пользователь с таким Id");
        }
        User cloneUser = users.get(user.getId()).clone();
        if (user.getEmail() != null) cloneUser.setEmail(user.getEmail());
        if (user.getBirthday() != null) cloneUser.setBirthday(user.getBirthday());
        if (user.getLogin() != null) cloneUser.setLogin(user.getLogin());
        if (user.getName() != null) cloneUser.setName(user.getName());
        try {
            cloneUser.validate();
            isNotDuplicate(cloneUser);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        saveUser(cloneUser);
        return cloneUser;
    }

    private int getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
