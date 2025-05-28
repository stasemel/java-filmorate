package ru.yandex.practicum.filmorate.storage.user;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@Getter
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private final Map<String, User> emails = new HashMap<>();
    private final Map<String, User> logins = new HashMap<>();

    @Override
    public Optional<User> createUser(User user) {
        addNewUser(user);
        log.info("Create user: {}", user);
        return Optional.of(user);
    }

    @Override
    public Optional<User> updateUser(User user) {
        if (user.getId() == null) return Optional.empty();
        saveUser(user);
        return Optional.of(user);
    }


    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public Optional<User> getUserById(int id) {
        if (users.containsKey(id)) {
            return Optional.of(users.get(id).cloneUser());
        }
        return Optional.empty();
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

    public boolean isNotDuplicate(User user) {
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

    @Override
    public void clearUsers() {
        users.clear();
        emails.clear();
        logins.clear();
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
