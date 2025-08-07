package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @GetMapping
    public Collection<User> findAll() {
        log.info("Запрос на получение всех пользователей");
        Collection<User> users = service.findAll();
        log.info("Возвращено {} пользователей", users.size());
        return users;
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        log.info("Запрос пользователя с ID: {}", id);
        User user = service.getUserById(id);
        log.info("Найден пользователь: {}", user);
        return user;
    }

    @GetMapping("/{id}/friends")
    public Collection<User> findAllFriend(@PathVariable Long id) {
        log.info("Запрос списка друзей пользователя с ID: {}", id);
        Collection<User> friends = service.findAllFriend(id);
        log.info("Возвращено {} друзей пользователя ID {}", friends.size(), id);
        return friends;
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable Long id,
                                             @PathVariable Long otherId) {
        log.info("Запрос общих друзей пользователей ID {} и ID {}", id, otherId);
        Collection<User> commonFriends = service.getCommonFriends(id, otherId);
        log.info("Найдено {} общих друзей между пользователями {} и {}", commonFriends.size(), id, otherId);
        return commonFriends;
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Запрос на создание пользователя: {}", user);
        User createdUser = service.create(user);
        log.info("Пользователь создан: {}", createdUser);
        return createdUser;
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        log.info("Запрос на обновление пользователя: {}", newUser);
        User updatedUser = service.update(newUser);
        log.info("Пользователь обновлен: {}", updatedUser);
        return updatedUser;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id,
                          @PathVariable Long friendId) {
        log.info("Запрос на добавление в друзья: пользователь {} добавляет пользователя {}", id, friendId);
        service.addFriend(id, friendId);
        log.info("Пользователь {} добавлен в друзья к пользователю {}", friendId, id);
    }

    @DeleteMapping({"/{id}"})
    public void delete(@PathVariable Long id) {
        log.info("Запрос на удаление пользователя с ID: {}", id);
        service.delete(id);
        log.info("Пользователь с ID {} удален", id);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long id,
                             @PathVariable Long friendId) {
        log.info("Запрос на удаление из друзей: пользователь {} удаляет пользователя {}", id, friendId);
        service.deleteFriend(id, friendId);
        log.info("Пользователь {} удален из друзей пользователя {}", friendId, id);
    }
}