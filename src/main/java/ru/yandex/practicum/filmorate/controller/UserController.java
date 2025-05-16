package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Data
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private final Map<String, User> emails = new HashMap<>();
    private final Map<String, User> logins = new HashMap<>();

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.debug("Receive new user: {}", user);
        try {
            if (user.validate()) {
                log.trace("Validate success");
            }
            if (isNotDuplicate(user)) {
                log.trace("Is not duplicated");
            }
        } catch (RuntimeException e) {
            log.debug("POST error: {}, user: {}", e, user);
            log.warn("POST error: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        if ((user.getName() == null) || (user.getName().isBlank())) user.setName(user.getLogin());
        addNewUser(user);
        log.info("Create user: {}", user);
        return user;
    }

    @GetMapping
    public Collection<User> findAll() {
        log.info("Get users");
        return users.values();
    }

    private void addNewUser(User user) {
        user.setId(getNextId());
        saveUser(user);
    }

    private void saveUser(User user) {
        users.put(user.getId(), user);
        emails.put(user.getEmail(), user);
        logins.put(user.getLogin(), user);
    }

    private boolean isNotDuplicate(User user) {
        String email = user.getEmail();
        String login = user.getLogin();
        Integer id = user.getId();
        if (emails.containsKey(email) && ((id == null) || (!emails.get(email).getId().equals(id)))) {
            throw new ValidationException(String.format("Пользователь с email %s уже существует", email));
        }
        if (logins.containsKey(login) && ((id == null) || (!logins.get(login).getId().equals(id)))) {
            throw new ValidationException(String.format("Пользователь с логином %s уже существует", login));
        }
        return true;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.debug("Receive update user {}", user);
        if (user.getId() == null) {
            log.warn("PUT error: empty id");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id должен быть указан");
        }
        if (!users.containsKey(user.getId())) {
            log.warn("PUT error: user not found by id {}", user.getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Не найден пользователь с id %d", user.getId()));
        }
        if ((user.getName() == null) && (user.getEmail() == null) && (user.getLogin() == null) && (user.getBirthday() == null)) {
            log.warn("PUT error: no data to change {}", user);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Нет данных для изменения");
        }
        User cloneUser = users.get(user.getId()).cloneUser();
        if (user.getEmail() != null) cloneUser.setEmail(user.getEmail());
        if (user.getBirthday() != null) cloneUser.setBirthday(user.getBirthday());
        if (user.getLogin() != null) cloneUser.setLogin(user.getLogin());
        if (user.getName() != null) cloneUser.setName(user.getName());
        try {
            if (cloneUser.validate()) {
                log.trace("Validate success");
            }
            if (isNotDuplicate(cloneUser)) {
                log.trace("Is not duplicated");
            }
        } catch (RuntimeException e) {
            log.warn("PUT error: {}", e.getMessage());
            log.debug("PUT error: {}, user: {}", e, user);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        saveUser(cloneUser);
        log.info("Update user: {}", cloneUser);
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
