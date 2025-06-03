package ru.yandex.practicum.filmorate.storage.user;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
@Getter
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
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
    public Optional<User> getUserById(Long id) {
        if (users.containsKey(id)) {
            return Optional.of(users.get(id).cloneUser());
        }
        throw new NotFoundException(String.format("Не найден пользователь с id %d", id));
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
        Long id = user.getId();
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

    @Override
    public void addFriend(Long userId, Long friendId) {
        String errorText = "Не найден пользователь с id %d";
        Optional<User> optionalUser = getUserById(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException(String.format(errorText, userId));
        }
        Optional<User> optionalFriend = getUserById(friendId);
        if (optionalFriend.isEmpty()) {
            throw new NotFoundException(String.format(errorText, friendId));
        }
        users.get(userId).getFriends().add(friendId);
        users.get(friendId).getFriends().add(userId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        String errorText = "Не найден пользователь с id %d";
        Optional<User> optionalUser = getUserById(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException(String.format(errorText, userId));
        }
        Optional<User> optionalFriend = getUserById(friendId);
        if (optionalFriend.isEmpty()) {
            throw new NotFoundException(String.format(errorText, friendId));
        }
        users.get(userId).getFriends().remove(friendId);
        users.get(friendId).getFriends().remove(userId);
    }

    @Override
    public List<User> getFriends(Long userId) {
        Optional<User> optionalUser = getUserById(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException(String.format("Не найден пользователь с id %d", userId));
        }
        return optionalUser.get().getFriends().stream().map(users::get).collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        String errorText = "Не найден пользователь с id %d";
        Optional<User> optionalUser = getUserById(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException(String.format(errorText, userId));
        }
        Optional<User> optionalOtherUser = getUserById(otherUserId);
        if (optionalOtherUser.isEmpty()) {
            throw new NotFoundException(String.format(errorText, otherUserId));
        }
        return optionalUser.get().getFriends().stream()
                .filter(id -> optionalOtherUser.get().getFriends().contains(id))
                .map(users::get)
                .collect(Collectors.toList());
    }

    @Override
    public void likeFilmByUser(Long userId, Long filmId) {
        Optional<User> optionalUser = getUserById(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException(String.format("Не найден пользователь с id %d", userId));
        }
        optionalUser.get().getLikedFilms().add(filmId);
    }

    @Override
    public void deleteLikeFilmByUser(Long userId, Long filmId) {
        Optional<User> optionalUser = getUserById(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException(String.format("Не найден пользователь с id %d", userId));
        }
        optionalUser.get().getLikedFilms().remove(filmId);
    }

    private Long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
