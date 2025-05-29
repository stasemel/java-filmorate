package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserStorage {
    Optional<User> createUser(User user);

    Optional<User> updateUser(User user);

    Collection<User> findAll();

    Optional<User> getUserById(Long id);

    boolean isNotDuplicate(User user);

    void clearUsers();

    void addFriend(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);

    List<User> getFriends(Long userId);

    List<User> getCommonFriends(Long userId, Long otherUserId);

    void likeFilmByUser(Long userId, Long filmId);
    void deleteLikeFilmByUser(Long userId, Long filmId);

}
