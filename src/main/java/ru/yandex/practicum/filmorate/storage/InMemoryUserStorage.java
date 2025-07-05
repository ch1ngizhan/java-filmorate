package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    public Collection<User> findAll() {
        log.info("Получен запрос на получение всех пользователей. Текущее количество: {}", users.size());
        return users.values();
    }

    @Override
    public User create(@RequestBody User user) {
        log.info("Получен запрос на создание нового пользователя: {}", user);
        // Проверка обязательных условий
        validEmail(user);
        validLogin(user);
        validName(user);
        validBirthday(user);
        // Установка дополнительных полей
        user.setId(getNextId());
        // Сохранение пользователя
        users.put(user.getId(), user);
        log.info("Создан новый пользователь с ID {}: {}", user.getId(), user);
        return user;
    }

    @Override
    public User update(@RequestBody User newUser) {
        log.info("Получен запрос на обновление пользователя с ID {}: {}", newUser.getId(), newUser);
        // Проверка обязательных полей
        if (newUser.getId() == null) {
            String errorMessage = "ID должен быть указан";
            log.error("Ошибка валидации при обновлении пользователя: {}", errorMessage);
            throw new ValidationException(errorMessage);
        }
        // Поиск существующего пользователя
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            log.debug("Найден пользователь для обновления: {}", oldUser);
            // Проверка и обновление email
            if (newUser.getEmail() != null && !newUser.getEmail().equals(oldUser.getEmail())) {
                validEmail(newUser);
                oldUser.setEmail(newUser.getEmail());
                log.debug("Обновлен email пользователя ID {}: {}", oldUser.getId(), oldUser.getEmail());
            }
            // Обновление остальных полей
            if (newUser.getLogin() != null && !newUser.getLogin().isBlank()) {
                validLogin(newUser);
                oldUser.setLogin(newUser.getLogin());
                log.debug("Обновлен login пользователя ID {}: {}", oldUser.getId(), oldUser.getLogin());

                if (oldUser.getName() == null || oldUser.getName().equals(oldUser.getLogin())) {
                    oldUser.setName(newUser.getLogin());
                    log.debug("Имя пользователя ID {} установлено равным login: {}", oldUser.getId(), oldUser.getName());
                }
            }
            if (newUser.getName() != null) {
                oldUser.setName(newUser.getName());
                log.debug("Обновлено имя пользователя ID {}: {}", oldUser.getId(), oldUser.getName());
            }
            if (newUser.getBirthday() != null) {
                validBirthday(newUser);
                oldUser.setBirthday(newUser.getBirthday());
                log.debug("Обновлена дата рождения пользователя ID {}: {}", oldUser.getId(), oldUser.getBirthday());
            }
            return oldUser;
        }
        String errorMessage = "Пользователь с ID = " + newUser.getId() + " не найден";
        log.error("Ошибка при обновлении пользователя: {}", errorMessage);
        throw new NotFoundException(errorMessage);
    }

    @Override
    public void delete(Long id) {
        log.info("Получен запрос на удаление User с ID {}: {}", id, users.get(id));
        if (id == null) {
            String errorMessage = "ID должен быть указан";
            log.error("Ошибка валидации при удалении User: {}", errorMessage);
            throw new ValidationException(errorMessage);
        }
        if (users.containsKey(id)) {
            User oldUser = users.get(id);
            log.debug("Найден User для удаления: {}", oldUser);
            users.remove(id);
        }
        String errorMessage = "User с id = " + id + " не найден";
        log.error("Ошибка при удалении user: {}", errorMessage);
        throw new NotFoundException(errorMessage);
    }

    public User getUserById(Long id) {
        validIdUsers(id); // Проверяем, что пользователь существует
        return users.get(id);
    }

    public void validIdUsers(Long id) {
        log.info("Получен запрос на поиск пользователя по ID: {}", id);
        if (id == null) {
            String errorMessage = "ID должен быть указан";
            log.error("Ошибка валидации: {}", errorMessage);
            throw new ValidationException(errorMessage);
        }
        if (users.containsKey(id)) {
            log.debug("Пользователь найден.");
            return;
        }
        String errorMessage = "User с id = " + id + " не найден";
        log.error("Ошибка : {}", errorMessage);
        throw new NotFoundException(errorMessage);
    }

    private void validEmail(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            String errorMessage = "Email должен быть указан";
            log.error("Ошибка валидации при создании пользователя: {}", errorMessage);
            throw new ValidationException(errorMessage);
        }
        // Проверка уникальности email
        boolean emailExists = users.values().stream()
                .filter(u -> !u.getId().equals(user.getId()))
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
