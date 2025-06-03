package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

@Slf4j
@Data
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.debug("Receive new user: {}", user);
        return service.createUser(user);
    }

    @GetMapping
    public Collection<User> findAll() {
        log.info("Get users");
        return service.findAll();
    }

    @PutMapping(value = {"/{id}/friends/{friendId}"})
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        service.addFriend(id, friendId);
    }

    @DeleteMapping(value = {"/{id}/friends/{friendId}"})
    public void deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        service.deleteFriend(id, friendId);
    }

    @GetMapping(value = {"/{id}/friends"})
    public List<User> getFriends(@PathVariable Long id) {
        return service.getFriends(id);
    }

    @GetMapping(value = {"/{id}/friends/common/{otherId}"})
    public List<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return service.getCommonFriends(id, otherId);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.debug("Receive update user {}", user);
        return service.updateUser(user);
    }


}
