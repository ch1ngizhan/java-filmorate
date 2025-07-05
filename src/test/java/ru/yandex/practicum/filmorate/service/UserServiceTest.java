package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserServiceTest {
    private UserService service;
    private InMemoryUserStorage storage;
    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    void setUp() {
        storage = new InMemoryUserStorage();
        service = new UserService();

        // Установка зависимости через рефлексию
        setField(service, "storage", storage);

        // Создание тестовых пользователей
        user1 = createTestUser("user1@mail.ru", "user1");
        user2 = createTestUser("user2@mail.ru", "user2");
        user3 = createTestUser("user3@mail.ru", "user3");
    }

    private User createTestUser(String email, String login) {
        User user = User.builder()
                .email(email)
                .login(login)
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        return storage.create(user);
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void addFriend_ValidIds_Success() {
        service.addFriend(user1.getId(), user2.getId());

        Collection<User> friends = service.findAllFriend(user1.getId());
        assertEquals(1, friends.size());
        assertTrue(friends.stream().anyMatch(u -> u.getId().equals(user2.getId())));
    }

    @Test
    void addFriend_SameUserIds_ThrowsException() {
        assertThrows(DuplicatedDataException.class,
                () -> service.addFriend(user1.getId(), user1.getId()));
    }

    @Test
    void addFriend_NonExistingUser_ThrowsException() {
        assertThrows(NotFoundException.class,
                () -> service.addFriend(999L, user2.getId()));
    }

    @Test
    void deleteFriend_ValidIds_Success() {
        service.addFriend(user1.getId(), user2.getId());
        service.deleteFriend(user1.getId(), user2.getId());

        Collection<User> friends = service.findAllFriend(user1.getId());
        assertTrue(friends.isEmpty());
    }

    @Test
    void deleteFriend_NotFriends_NoChanges() {
        service.addFriend(user1.getId(), user2.getId());
        service.deleteFriend(user1.getId(), user3.getId());

        Collection<User> friends = service.findAllFriend(user1.getId());
        assertEquals(1, friends.size());
    }

    @Test
    void findAllFriend_ReturnsFriends() {
        service.addFriend(user1.getId(), user2.getId());
        service.addFriend(user1.getId(), user3.getId());

        Collection<User> friends = service.findAllFriend(user1.getId());
        assertEquals(2, friends.size());
        assertTrue(friends.stream().anyMatch(u -> u.getId().equals(user2.getId())));
        assertTrue(friends.stream().anyMatch(u -> u.getId().equals(user3.getId())));
    }

    @Test
    void findAllFriend_NoFriends_ReturnsEmpty() {
        Collection<User> friends = service.findAllFriend(user1.getId());
        assertTrue(friends.isEmpty());
    }

    @Test
    void getCommonFriends_ReturnsCommonFriends() {
        // user1 дружит с user2 и user3
        service.addFriend(user1.getId(), user2.getId());
        service.addFriend(user1.getId(), user3.getId());

        // user2 дружит с user3
        service.addFriend(user2.getId(), user3.getId());

        Collection<User> common = service.getCommonFriends(user1.getId(), user2.getId());
        assertEquals(1, common.size());
        assertEquals(user3.getId(), common.iterator().next().getId());
    }

    @Test
    void getCommonFriends_NoCommonFriends_ReturnsEmpty() {
        service.addFriend(user1.getId(), user2.getId());
        service.addFriend(user3.getId(), user1.getId());

        Collection<User> common = service.getCommonFriends(user1.getId(), user3.getId());
        assertTrue(common.isEmpty());
    }

    @Test
    void getCommonFriends_SameUser_ReturnsFriends() {
        service.addFriend(user1.getId(), user2.getId());
        service.addFriend(user1.getId(), user3.getId());

        Collection<User> common = service.getCommonFriends(user1.getId(), user1.getId());
        assertEquals(2, common.size());
    }

    @Test
    void getCommonFriends_OneWithoutFriends_ReturnsEmpty() {
        service.addFriend(user1.getId(), user2.getId());

        Collection<User> common = service.getCommonFriends(user1.getId(), user3.getId());
        assertTrue(common.isEmpty());
    }
}