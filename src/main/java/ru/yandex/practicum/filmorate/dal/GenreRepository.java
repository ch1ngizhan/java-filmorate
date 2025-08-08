package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;
import java.util.Optional;

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
}
