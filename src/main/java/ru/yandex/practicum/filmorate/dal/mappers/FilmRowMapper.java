package ru.yandex.practicum.filmorate.dal.mappers;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@AllArgsConstructor
public class FilmRowMapper implements RowMapper<Film> {
    private final MpaStorage mpaRepository;

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = Film.builder().build();
        film.setId(rs.getLong("film_id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));

        java.sql.Date d = rs.getDate("release_date");
        if (d != null) {
            film.setReleaseDate(d.toLocalDate());
        }

        film.setDuration(rs.getLong("duration"));

        // Заполняем MPA если есть
        Integer mpaId = rs.getObject("mpa_rating_id", Integer.class);
        if (mpaId != null) {
            film.setMpaRating(mpaRepository.findById(mpaId)
                    .orElse(null));
        }

        return film;
    }
}

