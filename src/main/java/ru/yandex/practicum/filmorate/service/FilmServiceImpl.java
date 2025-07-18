package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Collection<Film> findAll() {
        log.info("Запрос на получение всех фильмов");
        Collection<Film> films = filmStorage.findAll();
        log.info("Возвращено {} фильмов", films.size());
        return films;
    }

    public Film create(Film film) {
        log.info("Запрос на создание нового фильма: {}", film);

        // проверяем выполнение необходимых условий
        validName(film);
        validDescription(film);
        validDuration(film);
        validReleaseDate(film);

        Film createdFilm = filmStorage.create(film);
        log.info("Фильм успешно создан: {}", createdFilm);
        return createdFilm;
    }

    public Film update(Film newFilm) {
        log.info("Запрос на обновление фильма: {}", newFilm);

        Film existingFilm = findFilmOrThrow(newFilm.getId());
        log.debug("Найден фильм для обновления: {}", existingFilm);

        if (newFilm.getDescription() != null) {
            validDescription(newFilm);
        }
        if (newFilm.getReleaseDate() != null) {
            validReleaseDate(newFilm);
        }
        if (newFilm.getDuration() != null) {
            validDuration(newFilm);
        }

        Film updatedFilm = filmStorage.update(newFilm);
        log.info("Фильм успешно обновлен: {}", updatedFilm);
        return updatedFilm;
    }

    public void delete(Long id) {
        log.info("Запрос на удаление фильма с ID: {}", id);
        findFilmOrThrow(id);
        filmStorage.delete(id);
        log.info("Фильм с ID {} успешно удален", id);
    }

    public Film getFilmById(Long id) {
        log.info("Запрос фильма по ID: {}", id);
        Film film = findFilmOrThrow(id);
        log.info("Найден фильм: {}", film);
        return film;
    }

    public void addLike(Long filmId, Long userId) {
        log.info("Запрос на добавление лайка фильму {} от пользователя {}", filmId, userId);

        findFilmOrThrow(filmId);
        findUserOrThrow(userId);

        filmStorage.addLike(filmId, userId);
        log.info("Лайк успешно добавлен фильму {} от пользователя {}", filmId, userId);
    }

    // Удаление лайка
    public void removeLike(Long filmId, Long userId) {
        log.info("Запрос на удаление лайка у фильма {} от пользователя {}", filmId, userId);

        findFilmOrThrow(filmId);
        findUserOrThrow(userId);

        filmStorage.removeLike(filmId, userId);
        log.info("Лайк успешно удалён у фильма {} от пользователя {}", filmId, userId);
    }

    public Collection<Film> getPopularFilms(int count) {
        log.info("Запрос {} популярных фильмов", count);

        if (count <= 0) {
            String errorMessage = "Количество фильмов должно быть положительным числом: " + count;
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        Collection<Film> films = filmStorage.getPopularFilms(count);
        log.info("Возвращено {} популярных фильмов", films.size());
        return films;
    }

    private void validReleaseDate(Film film) {
        if (film.getReleaseDate() == null || !LocalDate.of(1895, 12, 28)
                .isBefore(film.getReleaseDate())) {
            String errorMessage = "Дата релиза должна быть после 28 декабря 1895 года: " + film;
            log.error("Ошибка валидации: {}", errorMessage);
            throw new ValidationException(errorMessage);
        }
    }

    private void validName(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            String errorMessage = "Не указано название фильма: " + film;
            log.error("Ошибка валидации: {}", errorMessage);
            throw new ValidationException(errorMessage);
        }
    }

    private void validDescription(Film film) {
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            String errorMessage = "Превышено максимальное количество символов в описании (макс. 200): " + film;
            log.error("Ошибка валидации: {}", errorMessage);
            throw new ValidationException(errorMessage);
        }
    }

    private void validDuration(Film film) {
        if (film.getDuration() == null || film.getDuration() <= 0) {
            String errorMessage = "Продолжительность фильма должна быть положительным числом: " + film;
            log.error("Ошибка валидации: {}", errorMessage);
            throw new ValidationException(errorMessage);
        }
    }

    private Film findFilmOrThrow(Long filmId) {
        return filmStorage.getFilmById(filmId)
                .orElseThrow(() -> {
                    String errorMessage = "Фильм с id = " + filmId + " не найден";
                    log.error(errorMessage);
                    return new NotFoundException(errorMessage);
                });
    }

    private User findUserOrThrow(Long userId) {
        log.debug("Поиск пользователя по ID: {}", userId);
        return userStorage.getUserById(userId)
                .orElseThrow(() -> {
                    String errorMessage = "Пользователь с id = " + userId + " не найден";
                    log.error(errorMessage);
                    return new NotFoundException(errorMessage);
                });
    }
}