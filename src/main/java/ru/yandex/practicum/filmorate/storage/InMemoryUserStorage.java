package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private final Map<Long, Set<Long>> friendsId = new HashMap<>();

    public Collection<User> findAll() {
        log.info("Получен запрос на получение всех пользователей. Текущее количество: {}", users.size());
        return users.values();
    }

    @Override
    public User create(User user) {
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
    public User update(User newUser) {
        log.info("Получен запрос на обновление пользователя с ID {}: {}", newUser.getId(), newUser);
        User oldUser = findAll().stream()
                .filter(u -> u.getId().equals(newUser.getId()))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Пользователь с id {} не найден", newUser.getId());
                    return new NotFoundException("Пользователь с id " + newUser.getId() + " не найден");
                });
        users.put(oldUser.getId(), newUser);
        log.info("Пользователь c id {} обновлен", newUser.getId());
        return newUser;
        /*// Проверка обязательных полей
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
        throw new ElementNotFoundException(errorMessage);*/
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

    public Optional<User> getUserById(Long id) {
        validIdUsers(id);
        return Optional.ofNullable(users.get(id));
    }

    public Collection<User> findAllFriend(Long id) {
        log.info("Получен запрос на получение всех друзей пользователя {}.", id);
        validIdUsers(id);
        Set<Long> friends = friendsId.getOrDefault(id, Collections.emptySet());
        log.info("Получен список друзей пользователя {}. Количество: {}", id, friends.size());

        return friends.stream()
                .map(users::get)       // Преобразуем ID в пользователей
                .filter(Objects::nonNull) // Фильтруем отсутствующих пользователей
                .collect(Collectors.toList());
    }

    public void addFriend(Long userId, Long friendId) {
        log.info("Попытка добавить друзей: пользователь {} добавляет пользователя {}", userId, friendId);
        // Проверяем существование пользователей
        validIdUsers(userId);
        validIdUsers(friendId);
        // Проверяем, что это не один и тот же пользователь
        if (userId.equals(friendId)) {
            String errorMessage = "Пользователь не может добавить самого себя в друзья";
            log.warn("Ошибка : {}", errorMessage);
            throw new DuplicatedDataException(errorMessage);
        }

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

    public void deleteFriend(Long userId, Long friendId) {
        log.info("Попытка удалить друзей: пользователь {} удаляет пользователя {}", userId, friendId);
        // Проверяем существование пользователей
        validIdUsers(userId);
        validIdUsers(friendId);
        // Проверяем, что это не один и тот же пользователь
        if (userId.equals(friendId)) {
            String errorMessage = "Пользователь не может добавить самого себя в друзья";
            log.warn("Ошибка : {}", errorMessage);
            throw new DuplicatedDataException(errorMessage);
        }
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

    public Collection<User> getCommonFriends(Long id, Long otherId) {
        log.info("Получение общих друзей пользователей {} и {}", id, otherId);

        // Проверяем существование пользователей
        validIdUsers(id);
        validIdUsers(otherId);

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

    public void validIdUsers(Long id) {
        log.info("Получен запрос на поиск пользователя по ID: {}", id);
        if (!users.containsKey(id)) {
            throw new UserNotFoundException("User с id = " + id + " не найден");

        }
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
