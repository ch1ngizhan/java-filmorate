package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Repository
public class FilmRepository extends BaseRepository<Film> implements FilmStorage {

    public static final String INSERT_FILM_GENRES_QUERY = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
    public static final String DELETE_FILM_GENRES_QUERY = "DELETE FROM film_genre WHERE film_id = ?";
    private static final String FIND_ALL_QUERY = "SELECT f.*, r.name AS rating_name FROM films f JOIN mpa_rating r ON f.mpa_rating_id = r.mpa_rating_id";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE film_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO films (name, description, release_date, duration, mpa_rating_id) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_rating_id = ? " +
            "WHERE film_id = ?";
    private static final String ADD_LIKE_SQL =
            "INSERT INTO film_like (film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_LIKE_SQL =
            "DELETE FROM film_like WHERE film_id = ? AND user_id = ?";
    private static final String POPULAR_FILMS_SQL =
            "SELECT f.*, COUNT(ul.user_id) AS likes_count " +
                    "FROM films f " +
                    "LEFT JOIN film_like ul ON f.film_id = ul.film_id " +
                    "GROUP BY f.film_id " +
                    "ORDER BY likes_count DESC " +
                    "LIMIT ?";

    public FilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Film create(Film film) {
        long id = insert(INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpaRating().getId()
        );
        film.setId(id);

        jdbc.update(DELETE_FILM_GENRES_QUERY, id);
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                try {
                    jdbc.update(INSERT_FILM_GENRES_QUERY, id, genre.getId());
                } catch (DuplicateKeyException ignored) {
                    log.warn("Попытка добавить дублирующийся жанр {} для фильма {}", genre.getName(), film.getName());
                }
            }
        }
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        int updated = jdbc.update(UPDATE_QUERY,
                newFilm.getName(),
                newFilm.getDescription(),
                newFilm.getReleaseDate(),
                newFilm.getDuration(),
                newFilm.getMpaRating().getId(),
                newFilm.getId()
        );
        if (updated == 0) {
            throw new NotFoundException("Фильм не найден");
        }

        jdbc.update(DELETE_FILM_GENRES_QUERY, newFilm.getId());
        if (newFilm.getGenres() != null) {
            for (Genre genre : newFilm.getGenres()) {
                try {
                    jdbc.update(INSERT_FILM_GENRES_QUERY, newFilm.getId(), genre.getId());
                } catch (DuplicateKeyException ignored) {
                    log.warn("Попытка добавить дублирующийся жанр {} для фильма {}", genre.getName(), newFilm.getName());
                }
            }
        }
        return newFilm;
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        jdbc.update(ADD_LIKE_SQL, filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        jdbc.update(DELETE_LIKE_SQL, filmId, userId);
    }

    @Override
    public Collection<Film> getPopularFilms(int count) {
        return jdbc.query(POPULAR_FILMS_SQL, mapper, count);
    }

    @Override
    public Collection<Film> findAll() {
        return jdbc.query(FIND_ALL_QUERY, mapper);

    }

}
