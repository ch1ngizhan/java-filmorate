package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Optional;

@Repository
public class UserRepository extends BaseRepository<User> implements UserStorage {
    public static final String DELETE_FRIENDSHIP_QUERY = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?";
    public static final String FIND_FRIENDS_QUERY = "SELECT u.* FROM users u " +
            "JOIN friendships f ON u.user_id = f.friend_id " +
            "WHERE f.user_id = ?";
    public static final String FIND_COMMON_FRIENDS_QUERY = "SELECT u.* FROM users u " +
            "JOIN friendships f1 ON u.user_id = f1.friend_id " +
            "JOIN friendships f2 ON u.user_id = f2.friend_id " +
            "WHERE f1.user_id = ? AND f2.user_id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE user_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ?" +
            " WHERE user_id = ?";
    private static final String INSERT_FRIENDSHIP_QUERY = "INSERT INTO friendships (user_id, friend_id, status) " +
            "VALUES (?, ?, 'PENDING')";

    public UserRepository(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public User create(User user) {
        long id = insert(INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);
        return user;
    }

    @Override
    public User update(User user) {
        update(
                UPDATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        return user;
    }

    @Override
    public void delete(Long id) {
        delete("DELETE FROM users WHERE user_id = ?", id);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        jdbc.update(INSERT_FRIENDSHIP_QUERY, userId, friendId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        jdbc.update(DELETE_FRIENDSHIP_QUERY, userId, friendId);
    }

    @Override
    public Collection<User> getCommonFriends(Long id, Long otherId) {
        return findMany(FIND_COMMON_FRIENDS_QUERY, id, otherId);
    }

    @Override
    public Collection<User> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    @Override
    public Collection<User> findAllFriend(Long id) {
        return findMany(FIND_FRIENDS_QUERY, id);
    }
}
