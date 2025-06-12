package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController();
    }

    @Test
    void create_ShouldThrowException_WhenUserIsNull() {
        assertThrows(ValidationException.class, () -> userController.create(null),
                "Должно выбрасываться исключение при попытке создать null пользователя");
    }

    @Test
    void create_ShouldThrowException_WhenEmailIsBlank() {
        User user = User.builder()
                .email("")
                .login("login")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        assertThrows(ValidationException.class, () -> userController.create(user),
                "Должно выбрасываться исключение при пустом email");
    }

    @Test
    void create_ShouldThrowException_WhenEmailWithoutAt() {
        User user = User.builder()
                .email("emailwithoutat")
                .login("login")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        assertThrows(ValidationException.class, () -> userController.create(user),
                "Должно выбрасываться исключение при email без @");
    }

    @Test
    void create_ShouldThrowException_WhenLoginIsBlank() {
        User user = User.builder()
                .email("email@example.com")
                .login("")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        assertThrows(ValidationException.class, () -> userController.create(user),
                "Должно выбрасываться исключение при пустом логине");
    }

    @Test
    void create_ShouldThrowException_WhenBirthdayInFuture() {
        User user = User.builder()
                .email("email@example.com")
                .login("login")
                .birthday(LocalDate.now().plusDays(1))
                .build();

        assertThrows(ValidationException.class, () -> userController.create(user),
                "Должно выбрасываться исключение при дате рождения в будущем");
    }

    @Test
    void create_ShouldSetLoginAsName_WhenNameIsNull() {
        User user = User.builder()
                .email("email@example.com")
                .login("login")
                .birthday(LocalDate.of(1990, 1, 1))
                .name(null)
                .build();

        User createdUser = userController.create(user);
        assertEquals(user.getLogin(), createdUser.getName(),
                "Имя должно быть равно логину, если имя не указано");
    }

    @Test
    void update_ShouldThrowException_WhenUserIsNull() {
        assertThrows(ValidationException.class, () -> userController.update(null),
                "Должно выбрасываться исключение при попытке обновить null пользователя");
    }
}
