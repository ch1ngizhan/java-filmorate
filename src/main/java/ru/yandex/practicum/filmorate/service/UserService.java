package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final InMemoryUserStorage storage;

    public Collection<User> findAllFriend(Long id) {
        return findAllFriend(id);
    }

    public User getUserById(Long userId) {
        return storage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
    }

    public void addFriend(Long userId, Long friendId) {
        storage.addFriend(userId, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        storage.deleteFriend(userId, friendId);
    }

    public Collection<User> getCommonFriends(Long id, Long otherId) {
        return storage.getCommonFriends(id, otherId);
    }

    public Collection<User> findAll() {
        return storage.findAll();
    }

    public User create(User user) {
        return storage.create(user);
    }

    public User update(User newUser) {
        return storage.update(newUser);
    }

    public void delete(Long id) {
        storage.delete(id);
    }


}
