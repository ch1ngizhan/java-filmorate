package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserService {
    Collection<User> findAllFriend(Long id);

    User getUserById(Long userId);

    void addFriend(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);

    Collection<User> getCommonFriends(Long id, Long otherId);

    Collection<User> findAll();

    User create(User user);

    User update(User newUser);

    void delete(Long id);

}
