package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.ElementNotFoundException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private final Map<Long, Set<Long>> likesId = new HashMap<>();

    public Collection<Film> findAll() {
        log.info("Получен запрос на получение всех фильмов. Текущее количество: {}", films.size());
        return films.values();
    }

    @Override
    public Film create(Film film) {
        log.info("Получен запрос на создание нового фильма: {}", film);
        // проверяем выполнение необходимых условий
        validName(film);
        validDescription(film);
        validDuration(film);
        validReleaseDate(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Создан новый фильм с ID {}: {}", film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        log.info("Получен запрос на обновление фильма с ID {}: {}", newFilm.getId(), newFilm);
        Film oldFilm = findAll().stream()
                .filter(f -> f.getId().equals(newFilm.getId()))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Фильм с id {} не найден", newFilm.getId());
                    return new NotFoundException("Фильм с id " + newFilm.getId() + " не найден");
                });


        films.put(oldFilm.getId(), newFilm);
        log.info("Фильм c id {} обновлен", newFilm.getId());
        return newFilm;
       /* if (newFilm.getId() == null) {
            String errorMessage = "ID должен быть указан";
            log.error("Ошибка валидации при обновлении фильма: {}", errorMessage);
            throw new ValidationException(errorMessage);
        }
        // проверяем выполнение необходимых условий
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            log.debug("Найден фильм для обновления: {}", oldFilm);
            // Обновление полей
            if (newFilm.getName() != null && !newFilm.getName().isBlank()) {
                oldFilm.setName(newFilm.getName());
                log.debug("Обновлено название фильма ID {}: {}", oldFilm.getId(), oldFilm.getName());
            }
            if (newFilm.getDescription() != null) {
                validDescription(newFilm);
                oldFilm.setDescription(newFilm.getDescription());
                log.debug("Обновлено описание фильма ID {}", oldFilm.getId());
            }
            if (newFilm.getReleaseDate() != null) {
                validReleaseDate(newFilm);
                oldFilm.setReleaseDate(newFilm.getReleaseDate());
                log.debug("Обновлена дата релиза фильма ID {}: {}", oldFilm.getId(), oldFilm.getReleaseDate());
            }
            if (newFilm.getDuration() != null) {
                validDuration(newFilm);
                oldFilm.setDuration(newFilm.getDuration());
                log.debug("Обновлена продолжительность фильма ID {}: {}", oldFilm.getId(), oldFilm.getDuration());
            }
            log.info("Фильм с ID {} успешно обновлен: {}", oldFilm.getId(), oldFilm);
            return oldFilm;
        }
        String errorMessage = "Фильм с id = " + newFilm.getId() + " не найден";
        log.error("Ошибка при обновлении фильма: {}", errorMessage);
        throw new ElementNotFoundException(errorMessage);*/
    }


    @Override
    public void delete(Long id) {
        log.info("Получен запрос на удаление фильма с ID {}: {}", id, films.get(id));
        if (id == null) {
            String errorMessage = "ID должен быть указан";
            log.error("Ошибка валидации при удаление фильма: {}", errorMessage);
            throw new ValidationException(errorMessage);
        }
        if (films.containsKey(id)) {
            Film oldFilm = films.get(id);
            log.debug("Найден фильм для удаления: {}", oldFilm);
            films.remove(id);
            return;
        }
        String errorMessage = "Фильм с id = " + id + " не найден";
        log.error("Ошибка при удалении фильма: {}", errorMessage);
        throw new ElementNotFoundException(errorMessage);
    }

    public Optional<Film> getFilmById(Long id) {
        validIdFilm(id); // Проверяем, что фильм существует
        return Optional.ofNullable(films.get(id));
    }

    public void addLike(Long filmId, Long userId) {
        log.info("Пользователь {} пытается поставить лайк фильму {}", userId, filmId);
        validIdFilm(filmId);
        Set<Long> likes = likesId.getOrDefault(filmId, new HashSet<>());
        if (likes.contains(userId)) {
            String errorMessage = String.format("Пользователь %s уже поставил лайк фильму %s", userId, filmId);
            log.warn(errorMessage);
            throw new DuplicatedDataException(errorMessage);
        }
        likes.add(userId);
        int count = likes.size();
        log.debug("Пользователь {} успешно поставил лайк фильму {}.Кол-во лайков : {} ", userId, filmId, count);
        likesId.put(filmId, likes);
        films.get(filmId).setLikesCount(count);
    }

    // Удаление лайка
    public void removeLike(Long filmId, Long userId) {
        log.info("Попытка удаления лайка: пользователь {} удаляет лайк с фильма {}", userId, filmId);
        validIdFilm(filmId);
        Set<Long> likes = likesId.getOrDefault(filmId, new HashSet<>());
        // Проверяем существование лайка
        if (!likes.contains(userId)) {
            String errorMessage = String.format(
                    "Лайк не найден: пользователь %s не ставил лайк фильму %s",
                    userId, filmId
            );
            log.warn(errorMessage);
            throw new NotFoundException(errorMessage);
        }
        // Удаляем лайк
        likes.remove(userId);
        int count = likes.size();
        log.debug("Лайк успешно удален: пользователь {} убрал лайк с фильма {}.Кол-во лайков : {} ", userId, filmId,
                count);
        likesId.put(filmId, likes);
        films.get(filmId).setLikesCount(count);
    }

    public Collection<Film> getPopularFilms(int count) {
        return findAll().stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getLikesCount() == null ? 0 : film.getLikesCount())
                        .reversed())
                .limit(count)
                .toList();
    }

    public void validIdFilm(Long id) {
        log.info("Получен запрос на поиск фильма по ID: {}", id);
        if (id == null) {
            String errorMessage = "ID должен быть указан";
            log.error("Ошибка валидации: {}", errorMessage);
            throw new ValidationException(errorMessage);
        }
        if (films.containsKey(id)) {
            log.debug("Пользователь найден.");
            return;
        }
        String errorMessage = "Film с id = " + id + " не найден";
        log.error("Ошибка : {}", errorMessage);
        throw new ElementNotFoundException(errorMessage);
    }

    private void validReleaseDate(Film film) {
        if (film.getReleaseDate() == null || !LocalDate.of(1895, 12, 28)
                .isBefore(film.getReleaseDate())) {
            String errorMessage = "Дата релиза должна быть после 28 декабря 1895 года";
            log.error("Ошибка валидации при создании фильма: {}", errorMessage);
            throw new ValidationException(errorMessage);
        }
    }

    private void validName(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            String errorMessage = "Не указано название фильма!";
            log.error("Ошибка валидации при создании фильма: {}", errorMessage);
            throw new ValidationException(errorMessage);
        }
    }

    private void validDescription(Film film) {
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            String errorMessage = "Превышено максимальное количество символов в описании (макс. 200)";
            log.error("Ошибка валидации при создании фильма: {}", errorMessage);
            throw new ValidationException(errorMessage);
        }
    }


    private void validDuration(Film film) {
        if (film.getDuration() == null || film.getDuration() <= 0) {
            String errorMessage = "Продолжительность фильма должна быть положительным числом";
            log.error("Ошибка валидации при создании фильма: {}", errorMessage);
            throw new ValidationException(errorMessage);
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
