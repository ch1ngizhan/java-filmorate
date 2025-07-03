package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
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

    private void validEmail(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            String errorMessage = "Email должен быть указан";
            log.error("Ошибка валидации при создании пользователя: {}", errorMessage);
            throw new ValidationException(errorMessage);
        }
        // Проверка уникальности email
        boolean emailExists = users.values()
                .stream()
                .anyMatch(u -> u.getEmail().equals(user.getEmail()));
        if (emailExists) {
            String errorMessage = "Этот email уже используется: " + user.getEmail();
            log.error("Ошибка при создании пользователя: {}", errorMessage);
            throw new DuplicatedDataException(errorMessage);
        }
    }

    private void validLogin(User user) {
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            String errorMessage = "Логин обязателен и не может быть пустым";
            log.error("Ошибка валидации при создании пользователя: {}", errorMessage);
            throw new ValidationException(errorMessage);
        }
    }

    private void validName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Имя пользователя не указано, будет использован логин: {}", user.getLogin());
            user.setName(user.getLogin());
        }
    }

    private void validBirthday(User user) {
        if (user.getBirthday().isAfter(LocalDate.now())) {
            String errorMessage = "Дата рождения не может быть в будущем: " + user.getBirthday();
            log.error("Ошибка валидации при создании пользователя: {}", errorMessage);
            throw new ValidationException(errorMessage);
        }
    }

    // Вспомогательный метод для генерации ID нового пользователя
    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}

