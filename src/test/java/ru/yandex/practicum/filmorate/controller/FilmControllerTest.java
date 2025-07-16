package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {

    private FilmController filmController;

    @BeforeEach
    void setUp() {
        filmController = new FilmController(new FilmService(new InMemoryFilmStorage(), new InMemoryUserStorage()));
    }


    @Test
    void create_ShouldThrowException_WhenNameIsBlank() {
        Film film = Film.builder()
                .name("")
                .description("Description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120L)
                .build();

        assertThrows(ValidationException.class, () -> filmController.create(film),
                "Должно выбрасываться исключение при пустом названии");
    }

    @Test
    void create_ShouldThrowException_WhenDescriptionTooLong() {
        String longDescription = "a".repeat(201);
        Film film = Film.builder()
                .name("Name")
                .description(longDescription)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120L)
                .build();

        assertThrows(ValidationException.class, () -> filmController.create(film),
                "Должно выбрасываться исключение при описании длиннее 200 символов");
    }

    @Test
    void create_ShouldThrowException_WhenReleaseDateBefore1895() {
        Film film = Film.builder()
                .name("Name")
                .description("Description")
                .releaseDate(LocalDate.of(1895, 12, 27))
                .duration(120L)
                .build();

        assertThrows(ValidationException.class, () -> filmController.create(film),
                "Должно выбрасываться исключение при дате релиза раньше 28 декабря 1895");
    }

    @Test
    void create_ShouldThrowException_WhenDurationIsNegative() {
        Film film = Film.builder()
                .name("Name")
                .description("Description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(-120L)
                .build();

        assertThrows(ValidationException.class, () -> filmController.create(film),
                "Должно выбрасываться исключение при отрицательной продолжительности");
    }

    @Test
    void create_ShouldThrowException_WhenDurationIsZero() {
        Film film = Film.builder()
                .name("Name")
                .description("Description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(0L)
                .build();

        assertThrows(ValidationException.class, () -> filmController.create(film),
                "Должно выбрасываться исключение при нулевой продолжительности");
    }


    @Test
    void update_ShouldThrowException_WhenIdIsNull() {
        Film film = Film.builder()
                .id(null)
                .name("Name")
                .description("Description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120L)
                .build();

        assertThrows(ValidationException.class, () -> filmController.update(film),
                "Должно выбрасываться исключение при обновлении фильма без ID");
    }
}