package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final InMemoryUserStorage storage;

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return storage.findAll();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        return storage.create(user);
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        return storage.update(newUser);
    }

    @DeleteMapping({"/{id}"})
    public Optional<User> delete (@PathVariable Long id) {
        return storage.delete(id);
    }
}

