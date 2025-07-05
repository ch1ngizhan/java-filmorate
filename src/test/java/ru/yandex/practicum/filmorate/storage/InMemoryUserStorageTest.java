package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryUserStorageTest {
    private InMemoryUserStorage storage;
    private User validUser;

    @BeforeEach
    void setUp() {
        storage = new InMemoryUserStorage();
        validUser = User.builder()
                .email("test@mail.ru")
                .login("testlogin")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
    }

    @Test
    void createUser_ValidData_SetsNameFromLogin() {
        User created = storage.create(validUser);
        assertEquals(validUser.getLogin(), created.getName());
    }

    @Test
    void updateUser_ValidData_Success() {
        User created = storage.create(validUser);
        User update = created.builder()
                .name("New Name")
                .email("new@mail.ru")
                .build();

        User updated = storage.update(update);
        assertEquals("New Name", updated.getName());
        assertEquals("new@mail.ru", updated.getEmail());
    }
}