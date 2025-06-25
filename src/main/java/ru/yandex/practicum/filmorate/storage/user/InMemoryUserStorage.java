package ru.yandex.practicum.filmorate.storage.user;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.FriendShip;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Repository("memoryUserStorage")
@Getter
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final Map<String, User> emails = new HashMap<>();
    private final Map<String, User> logins = new HashMap<>();
    private final Set<FriendShip> friendShips = new HashSet<>();

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
        if (getSavedFriend(userId, friendId).isEmpty()) {
            friendShips.add(new FriendShip(userId, friendId));
            confirmFriend(userId, friendId);
        }
    }

    private Optional<FriendShip> getSavedFriend(Long userId, Long friendId) {
        FriendShip friendShip = new FriendShip(userId, friendId);
        List<FriendShip> list = friendShips.stream()
                .filter(f -> f.equals(friendShip))
                .toList();
        if (list.isEmpty()) return Optional.empty();
        return Optional.of(list.getFirst());
    }

    public void confirmFriend(Long userId, Long friendId) {
        Optional<FriendShip> optionalFriend = getSavedFriend(userId, friendId);
        if (optionalFriend.isEmpty()) {
            throw new NotFoundException("Не найдена запись о добавлении в друзья");
        }
        optionalFriend.get().setConfirm(true);
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
        Optional<User> optionalUserFriend = getUserById(friendId);
        if (optionalUserFriend.isEmpty()) {
            throw new NotFoundException(String.format(errorText, friendId));
        }
        Optional<FriendShip> optionalFriend = getSavedFriend(userId, friendId);
        if (optionalFriend.isEmpty()) return;
        friendShips.remove(optionalFriend.get());
        users.get(userId).getFriends().remove(friendId);
        users.get(friendId).getFriends().remove(userId);
    }

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
