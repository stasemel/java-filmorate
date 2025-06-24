package ru.yandex.practicum.filmorate.storage.user;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@Getter
public class DBUserStorage implements UserStorage {
    private final UserRepository userRepository = new UserRepository(new JdbcTemplate(), new UserRowMapper());

    @Override
    public Optional<User> createUser(User user) {
        return Optional.of(userRepository.save(user));
    }

    @Override
    public Optional<User> updateUser(User user) {
        return Optional.empty();
    }

    @Override
    public Collection<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return Optional.empty();
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

    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {

    }

    @Override
    public List<User> getFriends(Long userId) {
        return null;
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        return null;
    }

    @Override
    public void likeFilmByUser(Long userId, Long filmId) {

    }

    @Override
    public void deleteLikeFilmByUser(Long userId, Long filmId) {

    }
}
