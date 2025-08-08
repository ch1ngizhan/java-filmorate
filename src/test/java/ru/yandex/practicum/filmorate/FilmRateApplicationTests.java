package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dal.MpaRepository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({
        FilmRepository.class,
        UserRepository.class,
        GenreRepository.class,
        MpaRepository.class,
        ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper.class,
        ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper.class,
        ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper.class,
        ru.yandex.practicum.filmorate.dal.mappers.MpaRowMapper.class
})
class FilmRateApplicationTests {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    private Long userId1, userId2;
    private Long filmId1, filmId2;

    @BeforeEach
    void initData() {
        User u1 = User.builder()
                .email("a@a.com").login("a").name("A").birthday(LocalDate.of(1980, 1, 1))
                .build();
        User u2 = User.builder()
                .email("b@b.com").login("b").name("B").birthday(LocalDate.of(1990, 2, 2))
                .build();
        userId1 = userStorage.create(u1).getId();
        userId2 = userStorage.create(u2).getId();

        Film f1 = Film.builder()
                .name("Film1")
                .description("Desc1")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(100L)
                .mpaRating(new MpaRating(1, "G"))
                .genres(new LinkedHashSet<>(Set.of(new Genre(1, "Комедия"))))
                .build();
        Film f2 = Film.builder()
                .name("Film2")
                .description("Desc2")
                .releaseDate(LocalDate.of(2010, 2, 2))
                .duration(120L)
                .mpaRating(new MpaRating(2, "PG"))
                .genres(new LinkedHashSet<>(Set.of(new Genre(2, "Драма"))))
                .build();
        filmId1 = filmStorage.create(f1).getId();
        filmId2 = filmStorage.create(f2).getId();
    }

    @Test
    void testAddUser() {
        User x = User.builder()
                .email("x@x.com").login("x").name("X").birthday(LocalDate.of(2001, 1, 1))
                .build();
        User saved = userStorage.create(x);

        Optional<User> found = userStorage.getUserById(saved.getId());
        assertThat(found).isPresent()
                .get()
                .hasFieldOrPropertyWithValue("login", "x")
                .hasFieldOrPropertyWithValue("email", "x@x.com")
                .hasFieldOrPropertyWithValue("name", "X");
    }

    @Test
    void testGetUserById() {
        Optional<User> opt = userStorage.getUserById(userId1);
        assertThat(opt).isPresent()
                .get().hasFieldOrPropertyWithValue("id", userId1);
    }

    @Test
    void testGetAllUsers() {
        Collection<User> all = userStorage.findAll();
        assertThat(all).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void testUpdateUser() {
        User u = userStorage.getUserById(userId1).orElseThrow();
        u.setName("NewName");
        u.setEmail("new@a.com");
        userStorage.update(u);

        assertThat(userStorage.getUserById(userId1))
                .get().hasFieldOrPropertyWithValue("name", "NewName")
                .hasFieldOrPropertyWithValue("email", "new@a.com");
    }

    @Test
    void testFriendsLifecycle() {
        assertThat(userStorage.findAllFriend(userId1)).isEmpty();

        userStorage.addFriend(userId1, userId2);
        assertThat(userStorage.findAllFriend(userId1))
                .hasSize(1)
                .extracting(User::getId)
                .containsExactly(userId2);

        userStorage.deleteFriend(userId1, userId2);
        assertThat(userStorage.findAllFriend(userId1)).isEmpty();
    }

    @Test
    void testAddFilm() {
        Film f = Film.builder()
                .name("Solaris")
                .description("Sci-fi")
                .releaseDate(LocalDate.of(1972, 5, 5))
                .duration(169L)
                .mpaRating(new MpaRating(2, "PG"))
                .genres(new LinkedHashSet<>(Set.of(new Genre(2, "Драма"), new Genre(4, "Триллер"))))
                .build();
        Film saved = filmStorage.create(f);

        Optional<Film> opt = filmStorage.getFilmById(saved.getId());
        assertThat(opt).isPresent()
                .get()
                .hasFieldOrPropertyWithValue("name", "Solaris")
                .hasFieldOrPropertyWithValue("description", "Sci-fi")
                .hasFieldOrPropertyWithValue("mpaRating", new MpaRating(2, "PG"));
    }

    @Test
    void testGetFilmById() {
        Optional<Film> opt = filmStorage.getFilmById(filmId1);
        assertThat(opt).isPresent()
                .get().hasFieldOrPropertyWithValue("id", filmId1);
    }

    @Test
    void testGetAllFilms() {
        Collection<Film> films = filmStorage.findAll();
        assertThat(films).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void testUpdateFilm() {
        Film f = filmStorage.getFilmById(filmId2).orElseThrow();
        f.setName("UpdatedName");
        filmStorage.update(f);

        assertThat(filmStorage.getFilmById(filmId2))
                .get().hasFieldOrPropertyWithValue("name", "UpdatedName");
    }

    @Test
    void testTopFilmsAndLikes() {
        filmStorage.addLike(filmId1, userId1);

        var top1 = filmStorage.getPopularFilms(1);
        assertThat(top1)
                .hasSize(1)
                .extracting(Film::getId)
                .containsExactly(filmId1);
    }

    @Test
    void testGenresAndMpaLoaded() {
        var allGenres = genreStorage.findAll();
        assertThat(allGenres).hasSizeGreaterThanOrEqualTo(2);

        var allMpa = mpaStorage.findAll();
        assertThat(allMpa).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void getAllMpa_returnsNonEmptyList() {
        Collection<MpaRating> list = mpaStorage.findAll();
        assertThat(list).isNotEmpty()
                .allSatisfy(m -> assertThat(m.getId()).isPositive());
    }

}
