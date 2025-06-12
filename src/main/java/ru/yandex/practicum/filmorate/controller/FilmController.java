package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Получен запрос на получение всех фильмов. Текущее количество: {}", films.size());
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        try {
            log.info("Получен запрос на создание нового фильма: {}", film);
            // проверяем выполнение необходимых условий
            if (film.getName() == null || film.getName().isBlank()) {
                String errorMessage = "Не указано название фильма!";
                log.error("Ошибка валидации при создании фильма: {}", errorMessage);
                throw new ValidationException(errorMessage);
            }
            if (film.getDescription() != null && film.getDescription().length() > 200) {
                String errorMessage = "Превышено максимальное количество символов в описании (макс. 200)";
                log.error("Ошибка валидации при создании фильма: {}", errorMessage);
                throw new ValidationException(errorMessage);
            }
            if (film.getReleaseDate() == null || !LocalDate.of(1895, 12, 28).isBefore(film.getReleaseDate())) {
                String errorMessage = "Дата релиза должна быть после 28 декабря 1895 года";
                log.error("Ошибка валидации при создании фильма: {}", errorMessage);
                throw new ValidationException(errorMessage);
            }
            if (film.getDuration() == null || film.getDuration().isNegative() || film.getDuration().isZero()) {
                String errorMessage = "Продолжительность фильма должна быть положительным числом";
                log.error("Ошибка валидации при создании фильма: {}", errorMessage);
                throw new ValidationException(errorMessage);
            }
            film.setId(getNextId());
            films.put(film.getId(), film);
            log.info("Создан новый фильм с ID {}: {}", film.getId(), film);
            return film;
        } catch (Exception e) {
            throw new ValidationException("Некоректный вод данных");
        }
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        try {
            log.info("Получен запрос на обновление фильма с ID {}: {}", newFilm.getId(), newFilm);
            if (newFilm.getId() == null) {
                String errorMessage = "ID должен быть указан";
                log.error("Ошибка валидации при обновлении фильма: {}", errorMessage);
                throw new ValidationException(errorMessage);
            }
            // проверяем выполнение необходимых условий
            if (films.containsKey(newFilm.getId())) {
                Film oldFilm = films.get(newFilm.getId());
                log.debug("Найден фильм для обновления: {}", oldFilm);
                // валидация новых данных
                if (newFilm.getDescription() != null && newFilm.getDescription().length() > 200) {
                    String errorMessage = "Превышено максимальное количество символов в описании (макс. 200)";
                    log.error("Ошибка валидации при обновлении фильма: {}", errorMessage);
                    throw new ValidationException(errorMessage);
                }

                if (newFilm.getReleaseDate() != null && !LocalDate.of(1895, 12, 28).isBefore(newFilm.getReleaseDate())) {
                    String errorMessage = "Дата релиза должна быть после 28 декабря 1895 года";
                    log.error("Ошибка валидации при обновлении фильма: {}", errorMessage);
                    throw new ValidationException(errorMessage);
                }

                if (newFilm.getDuration() != null && (newFilm.getDuration().isNegative() || newFilm.getDuration().isZero())) {
                    String errorMessage = "Продолжительность фильма должна быть положительным числом";
                    log.error("Ошибка валидации при обновлении фильма: {}", errorMessage);
                    throw new ValidationException(errorMessage);
                }
                // Обновление полей
                if (newFilm.getName() != null && !newFilm.getName().isBlank()) {
                    oldFilm.setName(newFilm.getName());
                    log.debug("Обновлено название фильма ID {}: {}", oldFilm.getId(), oldFilm.getName());
                }

                if (newFilm.getDescription() != null) {
                    oldFilm.setDescription(newFilm.getDescription());
                    log.debug("Обновлено описание фильма ID {}", oldFilm.getId());
                }

                if (newFilm.getReleaseDate() != null) {
                    oldFilm.setReleaseDate(newFilm.getReleaseDate());
                    log.debug("Обновлена дата релиза фильма ID {}: {}", oldFilm.getId(), oldFilm.getReleaseDate());
                }

                if (newFilm.getDuration() != null) {
                    oldFilm.setDuration(newFilm.getDuration());
                    log.debug("Обновлена продолжительность фильма ID {}: {}", oldFilm.getId(), oldFilm.getDuration());
                }

                log.info("Фильм с ID {} успешно обновлен: {}", oldFilm.getId(), oldFilm);
                return oldFilm;
            }
            String errorMessage = "Фильм с id = " + newFilm.getId() + " не найден";
            log.error("Ошибка при обновлении фильма: {}", errorMessage);
            throw new NotFoundException(errorMessage);
        } catch (Exception e) {
            throw new ValidationException("Некоректный вод данных");
        }
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
