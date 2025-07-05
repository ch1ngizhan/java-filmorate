package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryFilmStorageTest {
    private InMemoryFilmStorage storage;
    private Film validFilm;

    @BeforeEach
    void setUp() {
        storage = new InMemoryFilmStorage();
        validFilm = Film.builder()
                .name("Valid Film")
                .description("Valid Description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120L)
                .build();
    }

    @Test
    void createFilm_ValidData_Success() {
        Film created = storage.create(validFilm);
        assertNotNull(created.getId());
        assertEquals(1, created.getId());
        assertEquals(1, storage.findAll().size());
    }

    @Test
    void createFilm_InvalidName_ThrowsException() {
        Film film = validFilm.builder().name("").build();
        assertThrows(ValidationException.class, () -> storage.create(film));
    }

    @Test
    void updateFilm_ValidData_Success() {
        Film created = storage.create(validFilm);
        Film updated = created.builder()
                .name("Updated Name")
                .description("Updated Description")
                .build();

        Film result = storage.update(updated);
        assertEquals("Updated Name", result.getName());
        assertEquals("Updated Description", result.getDescription());
    }

    @Test
    void updateFilm_InvalidId_ThrowsException() {
        Film film = validFilm.builder().id(999L).build();
        assertThrows(NotFoundException.class, () -> storage.update(film));
    }

    @Test
    void deleteFilm_ValidId_Success() {
        Film created = storage.create(validFilm);
        Optional<Film> deleted = storage.delete(created.getId());
        assertTrue(deleted.isPresent());
        assertEquals(0, storage.findAll().size());
    }

    @Test
    void getFilmById_ValidId_ReturnsFilm() {
        Film created = storage.create(validFilm);
        Film found = storage.getFilmById(created.getId());
        assertEquals(created, found);
    }
}