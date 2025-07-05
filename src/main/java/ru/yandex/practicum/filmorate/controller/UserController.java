package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final InMemoryUserStorage storage;
    private final UserService service;

    @GetMapping
    public Collection<User> findAll() {
        return storage.findAll();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return storage.getUserById(id);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> findAllFriend(@PathVariable Long id) {
        return service.findAllFriend(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable Long id,
                                             @PathVariable Long otherId) {
        return service.getCommonFriends(id, otherId);
    }

    @PostMapping
    public User create(@RequestBody User user) {
        return storage.create(user);
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        return storage.update(newUser);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id,
                          @PathVariable Long friendId) {
        service.addFriend(id, friendId);
    }

    @DeleteMapping({"/{id}"})
    public void delete(@PathVariable Long id) {
        storage.delete(id);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long id,
                             @PathVariable Long friendId) {
        service.deleteFriend(id, friendId);
    }
}

