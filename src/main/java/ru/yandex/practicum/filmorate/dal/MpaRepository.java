package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;
import java.util.Optional;

@Repository
public class MpaRepository extends BaseRepository<MpaRating> implements MpaStorage {
    private static final String FIND_ALL = "SELECT * FROM mpa_rating";
    private static final String FIND_BY_ID = "SELECT * FROM mpa_rating WHERE mpa_rating_id = ?";

    public MpaRepository(JdbcTemplate jdbc, RowMapper<MpaRating> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Optional<MpaRating> findById(int id) {
        return findOne(FIND_BY_ID, id);
    }

    @Override
    public Collection<MpaRating> findAll() {
        return findMany(FIND_ALL);
    }
}
