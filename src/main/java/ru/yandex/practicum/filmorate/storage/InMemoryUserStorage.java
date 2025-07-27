package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ElementNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private final Map<Long, Set<Long>> friendsId = new HashMap<>();

    @Override
    public Collection<User> findAll() {
        log.info("Получен запрос на получение всех пользователей. Текущее количество: {}", users.size());
        return users.values();
    }

    @Override
    public User create(User user) {
        log.info("Получен запрос на создание нового пользователя: {}", user);
        // Установка дополнительных полей
        user.setId(getNextId());
        // Сохранение пользователя
        users.put(user.getId(), user);
        log.info("Создан новый пользователь с ID {}: {}", user.getId(), user);
        return user;
    }

    @Override
    public User update(User newUser) {
        log.info("Получен запрос на обновление пользователя с ID {}: {}", newUser.getId(), newUser);
        // Проверка обязательных полей
        // Поиск существующего пользователя
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            log.debug("Найден пользователь для обновления: {}", oldUser);
            // Проверка и обновление email
            if (newUser.getEmail() != null && !newUser.getEmail().equals(oldUser.getEmail())) {
                oldUser.setEmail(newUser.getEmail());
                log.debug("Обновлен email пользователя ID {}: {}", oldUser.getId(), oldUser.getEmail());
            }
            // Обновление остальных полей
            if (newUser.getLogin() != null && !newUser.getLogin().isBlank()) {
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
                oldUser.setBirthday(newUser.getBirthday());
                log.debug("Обновлена дата рождения пользователя ID {}: {}", oldUser.getId(), oldUser.getBirthday());
            }
            return oldUser;
        }
        String errorMessage = "Пользователь с ID = " + newUser.getId() + " не найден";
        log.error("Ошибка при обновлении пользователя: {}", errorMessage);
        throw new ElementNotFoundException(errorMessage);
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
            return;
        }
        String errorMessage = "User с id = " + id + " не найден";
        log.error("Ошибка при удалении user: {}", errorMessage);
        throw new ElementNotFoundException(errorMessage);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Collection<User> findAllFriend(Long id) {
        log.info("Получен запрос на получение всех друзей пользователя {}.", id);
        Set<Long> friends = friendsId.getOrDefault(id, Collections.emptySet());
        log.info("Получен список друзей пользователя {}. Количество: {}", id, friends.size());

        return friends.stream()
                .map(users::get)       // Преобразуем ID в пользователей
                .filter(Objects::nonNull) // Фильтруем отсутствующих пользователей
                .collect(Collectors.toList());
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        log.info("Попытка добавить друзей: пользователь {} добавляет пользователя {}", userId, friendId);
        // Проверяем, что это не один и тот же пользователь


        Set<Long> userF = friendsId.getOrDefault(userId, new HashSet<>());
        Set<Long> friendF = friendsId.getOrDefault(friendId, new HashSet<>());
        // Добавляем друзей
        boolean userAdded = userF.add(friendId);
        boolean friendAdded = friendF.add(userId);
        if (userAdded) {
            log.debug("Пользователь {} успешно добавлен в друзья пользователя {}", friendId, userId);
        } else {
            log.debug("Пользователь {} уже был в друзьях у пользователя {}", friendId, userId);
        }
        if (friendAdded) {
            log.debug("Пользователь {} успешно добавлен в друзья пользователя {}", userId, friendId);
        } else {
            log.debug("Пользователь {} уже был в друзьях у пользователя {}", userId, friendId);
        }

        friendsId.put(userId, userF);
        log.trace("Успешно добавлено для пользователя: {}", userId);
        friendsId.put(friendId, friendF);
        log.trace("Успешно добавлено для пользователя: {}", friendId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        log.info("Попытка удалить друзей: пользователь {} удаляет пользователя {}", userId, friendId);
        Set<Long> userF = friendsId.getOrDefault(userId, new HashSet<>());
        Set<Long> friendF = friendsId.getOrDefault(friendId, new HashSet<>());

        // Удаляем друзей
        boolean removedFromUser = userF.remove(friendId);
        boolean removedFromFriend = friendF.remove(userId);

        if (removedFromUser) {
            log.debug("Пользователь {} успешно удален из друзей пользователя {}", friendId, userId);
        } else {
            log.debug("Пользователь {} не найден в друзьях у пользователя {}", friendId, userId);
        }

        if (removedFromFriend) {
            log.debug("Пользователь {} успешно удален из друзей пользователя {}", userId, friendId);
        } else {
            log.debug("Пользователь {} не найден в друзьях у пользователя {}", userId, friendId);
        }

        friendsId.put(userId, userF);
        log.trace("Успешно удалено для пользователя: {}", userId);
        friendsId.put(friendId, friendF);
        log.trace("Успешно удалено для пользователя: {}", friendId);
    }

    @Override
    public Collection<User> getCommonFriends(Long id, Long otherId) {
        log.info("Получение общих друзей пользователей {} и {}", id, otherId);

        // Получаем множества ID друзей для каждого пользователя
        Set<Long> firstUserFriends = friendsId.getOrDefault(id, Collections.emptySet());
        Set<Long> secondUserFriends = friendsId.getOrDefault(otherId, Collections.emptySet());

        // Создаем копию первого множества и находим пересечение
        Set<Long> commonFriendIds = new HashSet<>(firstUserFriends);
        commonFriendIds.retainAll(secondUserFriends);

        log.debug("Найдено {} общих друзей", commonFriendIds.size());

        // Преобразуем ID в объекты User
        return commonFriendIds.stream()
                .map(users::get)
                .collect(Collectors.toList());
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
