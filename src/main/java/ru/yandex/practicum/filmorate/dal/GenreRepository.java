package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class GenreRepository extends BaseRepository<Genre> implements GenreStorage {
    private static final String FIND_ALL = "SELECT * FROM genres";
    private static final String FIND_BY_ID = "SELECT * FROM genres WHERE genre_id = ?";

    public GenreRepository(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Optional<Genre> findById(int id) {
        return findOne(FIND_BY_ID, id);
    }

    @Override
    public Collection<Genre> findAll() {
        return findMany(FIND_ALL);
    }

    @Override
    public Map<Long, Set<Genre>> getGenresForFilms(Collection<Long> filmIds) {
        if (filmIds.isEmpty()) {
            return Map.of();
        }

        String inSql = filmIds.stream()
                .map(id -> "?")
                .collect(Collectors.joining(", "));

        String sql = "SELECT fg.film_id, g.genre_id, g.name " +
                "FROM film_genre fg " +
                "JOIN genres g ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id IN (" + inSql + ")";

        return jdbc.query(sql, filmIds.toArray(), rs -> {
            Map<Long, Set<Genre>> result = new HashMap<>();
            while (rs.next()) {
                Long filmId = rs.getLong("film_id");
                Genre genre = new Genre(rs.getInt("genre_id"), rs.getString("name"));
                result.computeIfAbsent(filmId, k -> new HashSet<>()).add(genre);
            }
            return result;
        });
    }

}
