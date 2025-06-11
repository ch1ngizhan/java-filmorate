package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    // Вспомогательный метод для генерации ID нового пользователя
    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        // Проверка обязательных условий
        if (user.getEmail() == null || user.getEmail().isBlank() || user.getEmail().contains("@")) {
            throw new ValidationException("Email должен быть указан");
        }
        // Проверка уникальности email
        boolean emailExists = users.values()
                .stream()
                .anyMatch(u -> u.getEmail().equals(user.getEmail()));
        if (emailExists) {
            throw new ValidationException("Этот email уже используется");
        }
        if (user.getLogin() == null) {
            throw new ValidationException("Логин обязателен");
        }

        if (user.getLogin().isBlank()) {
            throw new ValidationException("Логин не может быть пустым или состоять из пробелов");
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }

        // Установка дополнительных полей
        user.setId(getNextId());
        // Сохранение пользователя
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        // Проверка обязательных полей
        if (newUser.getId() == null) {
            throw new ValidationException("ID должен быть указан");
        }

        // Поиск существующего пользователя
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            // Проверка и обновление email
            if (newUser.getEmail() != null && !newUser.getEmail().equals(oldUser.getEmail())) {
                boolean emailExists = users.values()
                        .stream()
                        .anyMatch(u -> u.getEmail().equals(newUser.getEmail()));
                if (emailExists) {
                    throw new DuplicatedDataException("Этот email уже используется");
                }
                oldUser.setEmail(newUser.getEmail());
            }

            // Обновление остальных полей
            if (newUser.getUsername() != null) {
                oldUser.setUsername(newUser.getUsername());
            }

            if (newUser.getPassword() != null) {
                oldUser.setPassword(newUser.getPassword());
            }

            return oldUser;
        }

        throw new NotFoundException("Пользователь с ID = " + newUser.getId() + " не найден");
    }
}
}
