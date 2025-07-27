package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    User create(User user);

    User update(User user);

    void delete(Long id);

    void addFriend(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);

    Collection<User> getCommonFriends(Long id, Long otherId);

    Collection<User> findAll();

    Optional<User> getUserById(Long id);

    Collection<User> findAllFriend(Long id);
}
