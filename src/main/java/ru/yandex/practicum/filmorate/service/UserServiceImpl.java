package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage storage;

    @Override
    public Collection<User> findAllFriend(Long id) {
        log.info("Запрос списка друзей пользователя ID: {}", id);
        findUserOrThrow(id);
        Collection<User> friends = storage.findAllFriend(id);
        log.info("Возвращено {} друзей для пользователя ID {}", friends.size(), id);
        return friends;
    }

    @Override
    public User getUserById(Long userId) {
        log.info("Запрос пользователя по ID: {}", userId);
        User user = findUserOrThrow(userId);
        log.info("Найден пользователь: {}", user);
        return user;
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        log.info("Запрос на добавление в друзья: {} -> {}", userId, friendId);

        findUserOrThrow(userId);
        findUserOrThrow(friendId);

        if (userId.equals(friendId)) {
            String errorMessage = "Пользователь не может добавить самого себя в друзья";
            log.warn("Ошибка добавления друга: {}", errorMessage);
            throw new DuplicatedDataException(errorMessage);
        }

        storage.addFriend(userId, friendId);
        log.info("Пользователь {} успешно добавлен в друзья пользователю {}", friendId, userId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        log.info("Запрос на удаление из друзей: {} -> {}", userId, friendId);

        findUserOrThrow(userId);
        findUserOrThrow(friendId);

        if (userId.equals(friendId)) {
            String errorMessage = "Пользователь не может удалить самого себя из друзей";
            log.warn("Ошибка удаления друга: {}", errorMessage);
            throw new DuplicatedDataException(errorMessage);
        }

        storage.deleteFriend(userId, friendId);
        log.info("Пользователь {} удален из друзей пользователя {}", friendId, userId);
    }

    @Override
    public Collection<User> getCommonFriends(Long id, Long otherId) {
        log.info("Запрос общих друзей между {} и {}", id, otherId);

        findUserOrThrow(id);
        findUserOrThrow(otherId);

        if (id.equals(otherId)) {
            String errorMessage = "Пользователь не может сравнить друзей самого себя";
            log.warn("Ошибка поиска общих друзей: {}", errorMessage);
            throw new DuplicatedDataException(errorMessage);
        }

        Collection<User> commonFriends = storage.getCommonFriends(id, otherId);
        log.info("Найдено {} общих друзей между пользователями {} и {}", commonFriends.size(), id, otherId);
        return commonFriends;
    }

    @Override
    public Collection<User> findAll() {
        log.info("Запрос всех пользователей");
        Collection<User> users = storage.findAll();
        log.info("Возвращено {} пользователей", users.size());
        return users;
    }

    @Override
    public User create(User user) {
        log.info("Запрос на создание пользователя: {}", user);

        validEmail(user);
        validLogin(user);
        validName(user);
        validBirthday(user);

        User createdUser = storage.create(user);
        log.info("Пользователь успешно создан: {}", createdUser);
        return createdUser;
    }

    @Override
    public User update(User newUser) {
        log.info("Запрос на обновление пользователя: {}", newUser);
        if (newUser.getId() == null) {
            String errorMessage = "ID пользователя не должен быть пустым";
            log.error("Ошибка валидации: {}", errorMessage);
            throw new ValidationException(errorMessage);
        }
        User existingUser = findUserOrThrow(newUser.getId());
        log.debug("Найден пользователь для обновления: {}", existingUser);

        if (newUser.getEmail() != null) {
            validEmail(newUser);
        }
        if (newUser.getLogin() != null && !newUser.getLogin().isBlank()) {
            validLogin(newUser);
        }
        if (newUser.getBirthday() != null) {
            validBirthday(newUser);
        }

        User updatedUser = storage.update(newUser);
        log.info("Пользователь успешно обновлен: {}", updatedUser);
        return updatedUser;
    }

    @Override
    public void delete(Long id) {
        log.info("Запрос на удаление пользователя ID: {}", id);
        findUserOrThrow(id);
        storage.delete(id);
        log.info("Пользователь ID {} успешно удален", id);
    }

    private void validEmail(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            String errorMessage = "Email должен быть указан и содержать '@': " + user.getEmail();
            log.error("Ошибка валидации email: {}", errorMessage);
            throw new ValidationException(errorMessage);
        }

        boolean emailExists = storage.findAll().stream()
                .filter(u -> !u.getId().equals(user.getId()))
                .anyMatch(u -> u.getEmail().equals(user.getEmail()));

        if (emailExists) {
            String errorMessage = "Email уже используется: " + user.getEmail();
            log.error("Конфликт email: {}", errorMessage);
            throw new DuplicatedDataException(errorMessage);
        }
    }

    private void validLogin(User user) {
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            String errorMessage = "Логин обязателен и не может быть пустым";
            log.error("Ошибка валидации логина: {}", errorMessage);
            throw new ValidationException(errorMessage);
        }

        if (user.getLogin().contains(" ")) {
            String errorMessage = "Логин не может содержать пробелы: " + user.getLogin();
            log.error("Ошибка валидации логина: {}", errorMessage);
            throw new ValidationException(errorMessage);
        }
    }

    private void validName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Имя пользователя не указано, установлен логин: {}", user.getLogin());
            user.setName(user.getLogin());
        }
    }

    private void validBirthday(User user) {
        if (user.getBirthday().isAfter(LocalDate.now())) {
            String errorMessage = "Дата рождения не может быть в будущем: " + user.getBirthday();
            log.error("Ошибка валидации даты рождения: {}", errorMessage);
            throw new ValidationException(errorMessage);
        }
    }

    private User findUserOrThrow(Long userId) {
        log.debug("Поиск пользователя по ID: {}", userId);
        return storage.getUserById(userId)
                .orElseThrow(() -> {
                    String errorMessage = "Пользователь с id = " + userId + " не найден";
                    log.error(errorMessage);
                    return new NotFoundException(errorMessage);
                });
    }
}