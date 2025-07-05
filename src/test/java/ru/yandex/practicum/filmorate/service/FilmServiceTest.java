package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmServiceTest {
    private FilmService service;
    private InMemoryFilmStorage filmStorage;
    private InMemoryUserStorage userStorage;
    private Film film;
    private User user;

    @BeforeEach
    void setUp() {
        filmStorage = new InMemoryFilmStorage();
        userStorage = new InMemoryUserStorage();
        service = new FilmService();

        // Инициализация через рефлексию
        setField(service, "filmStorage", filmStorage);
        setField(service, "userStorage", userStorage);

        film = Film.builder()
                .name("Test Film")
                .description("Test Description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120L)
                .likesCount(0)
                .build();

        user = User.builder()
                .email("test@mail.ru")
                .login("testlogin")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        filmStorage.create(film);
        userStorage.create(user);
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
    void addLike_ValidData_Success() {
        service.addLike(film.getId(), user.getId());
        Film updatedFilm = filmStorage.getFilmById(film.getId());
        assertEquals(1, updatedFilm.getLikesCount());
    }

    @Test
    void addLike_DuplicateLike_ThrowsException() {
        service.addLike(film.getId(), user.getId());
        assertThrows(DuplicatedDataException.class,
                () -> service.addLike(film.getId(), user.getId()));
    }

    @Test
    void removeLike_ValidData_Success() {
        service.addLike(film.getId(), user.getId());
        service.removeLike(film.getId(), user.getId());
        Film updatedFilm = filmStorage.getFilmById(film.getId());
        assertEquals(0, updatedFilm.getLikesCount());
    }

    @Test
    void getPopularFilms_ReturnsOrderedFilms() {
        // Создаем второй фильм с лайками
        Film film2 = film.builder().name("Film 2").build();
        filmStorage.create(film2);
        service.addLike(film2.getId(), user.getId());

        Collection<Film> popular = service.getPopularFilms(2);
        assertEquals(2, popular.size());
        assertEquals(film2.getId(), popular.iterator().next().getId());
    }
}