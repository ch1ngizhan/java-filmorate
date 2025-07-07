package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private InMemoryUserStorage storage;

    public Collection<User> findAllFriend(Long id) {
        log.info("Получен запрос на получение всех друзей пользователя.");
        storage.validIdUsers(id);
        Set<Long> friends = storage.getUserById(id).getFriends();
        return friends.stream()
                .map(storage::getUserById)
                .collect(Collectors.toList());
    }

    public void addFriend(Long userId, Long friendId) {
        log.info("Попытка добавить друзей: пользователь {} добавляет пользователя {}", userId, friendId);
        // Проверяем существование пользователей
        storage.validIdUsers(userId);
        storage.validIdUsers(friendId);
        // Проверяем, что это не один и тот же пользователь
        if (userId.equals(friendId)) {
            String errorMessage = "Пользователь не может добавить самого себя в друзья";
            log.warn("Ошибка : {}", errorMessage);
            throw new DuplicatedDataException(errorMessage);
        }
        User user = storage.getUserById(userId);
        User friend = storage.getUserById(friendId);
        Set<Long> userF = user.getFriends();
        Set<Long> friendF = friend.getFriends();
        // Добавляем друзей
        boolean youAdded = userF.add(friendId);
        boolean friendAdded = friendF.add(userId);
        if (youAdded) {
            log.debug("Пользователь {} успешно добавлен в друзья пользователя {}", friendId, userId);
        } else {
            log.debug("Пользователь {} уже был в друзьях у пользователя {}", friendId, userId);
        }
        if (friendAdded) {
            log.debug("Пользователь {} успешно добавлен в друзья пользователя {}", userId, friendId);
        } else {
            log.debug("Пользователь {} уже был в друзьях у пользователя {}", userId, friendId);
        }

        user.setFriends(userF);
        log.trace("Успешно добавлено для пользователя: {}", userId);
        friend.setFriends(friendF);
        log.trace("Успешно добавлено для пользователя: {}", friendId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        log.info("Попытка удалить друзей: пользователь {} добавляет пользователя {}", userId, friendId);
        // Проверяем существование пользователей
        storage.validIdUsers(userId);
        storage.validIdUsers(friendId);
        // Проверяем, что это не один и тот же пользователь
        if (userId.equals(friendId)) {
            String errorMessage = "Пользователь не может добавить самого себя в друзья";
            log.warn("Ошибка : {}", errorMessage);
            throw new DuplicatedDataException(errorMessage);
        }
        User user = storage.getUserById(userId);
        User friend = storage.getUserById(friendId);
        Set<Long> userF = user.getFriends();
        Set<Long> friendF = friend.getFriends();
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

        user.setFriends(userF);
        log.trace("Успешно удалено для пользователя: {}", userId);
        friend.setFriends(friendF);
        log.trace("Успешно удалено для пользователя: {}", friendId);
    }

    public Collection<User> getCommonFriends(Long id, Long otherId) {
        log.info("Получение общих друзей пользователей {} и {}", id, otherId);

        // Проверяем существование пользователей
        storage.validIdUsers(id);
        storage.validIdUsers(otherId);

        // Получаем множества ID друзей для каждого пользователя
        Set<Long> firstUserFriends = storage.getUserById(id).getFriends();
        Set<Long> secondUserFriends = storage.getUserById(otherId).getFriends();

        // Создаем копию первого множества и находим пересечение
        Set<Long> commonFriendIds = new HashSet<>(firstUserFriends);
        commonFriendIds.retainAll(secondUserFriends);

        log.debug("Найдено {} общих друзей", commonFriendIds.size());

        // Преобразуем ID в объекты User
        return commonFriendIds.stream()
                .map(storage::getUserById)
                .collect(Collectors.toList());
    }


}
