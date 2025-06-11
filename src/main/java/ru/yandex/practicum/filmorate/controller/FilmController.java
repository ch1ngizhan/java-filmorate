package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        // проверяем выполнение необходимых условий
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Не указано название фильма!");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Превышено максимальное количество символов. ");
        }
        if (!LocalDate.of(1895,12,28).isBefore(film.getReleaseDate())) {
            throw new ValidationException("Дата не валидна");
        }
        if (film.getDuration() == null || film.getDuration().isNegative() || film.getDuration().isZero()){
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        if (newFilm.getId() == null) {
            throw new ValidationException("ID должен быть указан");
        }
        // проверяем выполнение необходимых условий
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            if (newFilm.getDescription().length() > 200) {
                throw new ValidationException("Превышено максимальное количество символов. ");
            }
            if (!LocalDate.of(1895, 12, 28).isBefore(newFilm.getReleaseDate())) {
                throw new ValidationException("Дата не валидна");
            }
            if (newFilm.getDuration().isNegative() || newFilm.getDuration().isZero()) {
                throw new ValidationException("Продолжительность фильма должна быть положительным числом");
            }
            // Обновление остальных полей
            if (newFilm.getName() != null) {
                oldFilm.setName(newFilm.getName());
            }
            if (newFilm.getDescription() != null) {
                oldFilm.setDescription(newFilm.getDescription());
            }
            if (newFilm.getReleaseDate() != null) {
                oldFilm.setReleaseDate(newFilm.getReleaseDate());
            }
            if (newFilm.getDuration() != null) {
                oldFilm.setDuration(newFilm.getDuration());
            }
            return oldFilm;
        }

        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
