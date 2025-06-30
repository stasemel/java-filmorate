package ru.yandex.practicum.filmorate.storage.user;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository("dbUserStorage")
@Getter
public class DbUserStorage implements UserStorage {
    private final UserRepository userRepository;

    public DbUserStorage(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> createUser(User user) {
        return Optional.of(userRepository.save(user));
    }

    @Override
    public Optional<User> updateUser(User user) {
        return Optional.of(userRepository.updateUser(user));
    }

    @Override
    public Collection<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.getUserById(id);
    }

    @Override
    public boolean isNotDuplicate(User user) {
        return userRepository.isNotDuplicate(user);
    }

    @Override
    public void clearUsers() {

    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        if (!userRepository.isFriendshipExists(userId, friendId)) {
            userRepository.insertFriend(userId, friendId);
        }
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        if (userRepository.isFriendshipExists(userId, friendId)) {
            userRepository.deleteFriend(userId, friendId);
        }
    }

    @Override
    public List<User> getFriends(Long userId) {
        return userRepository.getFriendIdByUser(userId);
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        return userRepository.getCommonFriendIdByUser(userId, otherUserId);
    }

    @Override
    public void likeFilmByUser(Long userId, Long filmId) {
        //реализация в DBFilmStorage
    }

    @Override
    public void deleteLikeFilmByUser(Long userId, Long filmId) {
        //реализация в DBFilmStorage
    }
}
