package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.Set;

@Service
@Slf4j
public class UserService {
    private InMemoryUserStorage storage;

    public void addFriend (Long userId, Long friendId) {
        log.info("Попытка добавить друзей: пользователь {} добавляет пользователя {}", userId, friendId);
        // Проверяем существование пользователей
            storage.validIdUsers(userId);
            storage.validIdUsers(friendId);
        // Проверяем, что это не один и тот же пользователь
        if (userId.equals(friendId)) {
            log.warn("Пользователь {} пытается добавить самого себя в друзья", userId);
            return;
        }
        Set<Long> user = storage.getUserById(userId).getFriends();
        Set<Long> friend = storage.getUserById(friendId).getFriends();
        // Добавляем друзей
        boolean youAdded = user.add(friendId);
        boolean friendAdded = friend.add(userId);
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

            storage.getUserById(userId).setFriends(user);
            log.trace("Успешно добавлено для пользователя: {}",userId);
            storage.getUserById(friendId).setFriends(friend);
            log.trace("Успешно добавлено для пользователя: {}",friendId);
    }

    public void deleteFriend (Long userId, Long friendId) {
        log.info("Попытка удалить друзей: пользователь {} добавляет пользователя {}", userId, friendId);
        // Проверяем существование пользователей
        storage.validIdUsers(userId);
        storage.validIdUsers(friendId);
        // Проверяем, что это не один и тот же пользователь
        if (userId.equals(friendId)) {
            log.warn("Пользователь {} пытается удалить самого себя в друзья", userId);
            return;
        }
        Set<Long> user = storage.getUserById(userId).getFriends();
        Set<Long> friend = storage.getUserById(friendId).getFriends();
        // Удаляем друзей
        boolean youAdded = user.remove(friendId);
        boolean friendAdded = friend.remove(userId);
    }


}
