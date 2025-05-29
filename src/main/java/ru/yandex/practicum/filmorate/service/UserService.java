package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.SaveException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Getter
@Slf4j
public class UserService {
    private final UserStorage storage;

    @Autowired
    public UserService(UserStorage storage) {
        this.storage = storage;
    }

    public User createUser(User user) {
        try {
            if (user.validate()) {
                log.trace("Validate success");
            }
            if (isNotDuplicate(user)) {
                log.trace("Is not duplicated");
            }
        } catch (RuntimeException e) {
            log.debug("Create user validation error: {}, user: {}", e, user);
            log.warn("Create user validation error: {}", e.getMessage());
            throw new ValidationException(e.getMessage());
        }
        if ((user.getName() == null) || (user.getName().isBlank())) user.setName(user.getLogin());
        Optional<User> optionalUser = storage.createUser(user);
        if (optionalUser.isEmpty()) {
            log.warn("Ошибка при создании пользователя {}", user);
            throw new SaveException("Ошибка при создании пользователя");
        }
        return optionalUser.get();
    }

    public User updateUser(User user) {
        if (user.getId() == null) {
            log.warn("Update user error: empty id");
            throw new ValidationException("Id должен быть указан");
        }
        Optional<User> optionalUser = storage.getUserById(user.getId());
        if (optionalUser.isEmpty()) {
            log.warn("User not found by id {}", user.getId());
            throw new NotFoundException(String.format("Не найден пользователь с id %d", user.getId()));
        }
        if ((user.getName() == null) && (user.getEmail() == null) && (user.getLogin() == null) && (user.getBirthday() == null)) {
            log.warn("No data to change {}", user);
            throw new ValidationException("Нет данных для изменения");
        }
        User cloneUser = optionalUser.get();
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
        } catch (ValidationException e) {
            log.warn("Update user validation error: {}", e.getMessage());
            log.debug("Update user validation error: {}, user: {}", e, user);
            throw new ValidationException(e.getMessage());
        } catch (RuntimeException e) {
            log.debug("Update user validation runtime error: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        Optional<User> optionalSavedUser = storage.updateUser(cloneUser);
        if (optionalSavedUser.isEmpty()) {
            log.warn("Не удалось сохранить изменения пользователя {}", user);
            throw new SaveException("Не удалось сохранить изменения пользователя");
        }

        log.info("Update user: {}", cloneUser);
        return optionalSavedUser.get();
    }

    public Collection<User> findAll() {
        return storage.findAll();
    }

    private boolean isNotDuplicate(User user) {
        return storage.isNotDuplicate(user);
    }

    public void addFriend(Long userId, Long friendId) {
        Optional<User> optionalUser = storage.getUserById(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException(String.format("Не найден пользователь с id %d", userId));
        }
        Optional<User> optionalFriend = storage.getUserById(friendId);
        if (optionalFriend.isEmpty()) {
            throw new NotFoundException(String.format("Не найден пользователь с id %d", friendId));
        }
        storage.addFriend(userId, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        Optional<User> optionalUser = storage.getUserById(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException(String.format("Не найден пользователь с id %d", userId));
        }
        Optional<User> optionalFriend = storage.getUserById(friendId);
        if (optionalFriend.isEmpty()) {
            throw new NotFoundException(String.format("Не найден пользователь с id %d", friendId));
        }
        storage.deleteFriend(userId, friendId);
    }

    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        Optional<User> optionalUser = storage.getUserById(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException(String.format("Не найден пользователь с id %d", userId));
        }
        Optional<User> optionalOtherUser = storage.getUserById(otherUserId);
        if (optionalOtherUser.isEmpty()) {
            throw new NotFoundException(String.format("Не найден пользователь с id %d", otherUserId));
        }
        return storage.getCommonFriends(userId, otherUserId);
    }

    public List<User> getFriends(Long userId) {
        Optional<User> optionalUser = storage.getUserById(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException(String.format("Не найден пользователь с id %d", userId));
        }
        return storage.getFriends(userId);
    }
}
