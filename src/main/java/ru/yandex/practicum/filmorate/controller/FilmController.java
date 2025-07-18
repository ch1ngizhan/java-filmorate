package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService service;

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Запрос на получение всех фильмов");
        Collection<Film> films = service.findAll();
        log.info("Возвращено {} фильмов", films.size());
        return films;
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) {
        log.info("Запрос фильма с ID: {}", id);
        Film film = service.getFilmById(id);
        log.info("Найден фильм: {}", film);
        return film;
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(
            @RequestParam(defaultValue = "10", required = false) int count
    ) {
        log.info("Запрос {} популярных фильмов", count);
        Collection<Film> films = service.getPopularFilms(count);
        log.info("Возвращено {} популярных фильмов", films.size());
        return films;
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.info("Запрос на создание фильма: {}", film);
        Film createdFilm = service.create(film);
        log.info("Фильм создан: {}", createdFilm);
        return createdFilm;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        log.info("Запрос на обновление фильма: {}", newFilm);
        Film updatedFilm = service.update(newFilm);
        log.info("Фильм обновлен: {}", updatedFilm);
        return updatedFilm;
    }

    @PutMapping(("/{id}/like/{userId}"))
    public void addLike(@PathVariable Long id,
                        @PathVariable Long userId) {
        log.info("Запрос на добавление лайка фильму {} от пользователя {}", id, userId);
        service.addLike(id, userId);
        log.info("Лайк добавлен фильму {} от пользователя {}", id, userId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("Запрос на удаление фильма с ID: {}", id);
        service.delete(id);
        log.info("Фильм с ID {} удален", id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id,
                           @PathVariable Long userId) {
        log.info("Запрос на удаление лайка у фильма {} от пользователя {}", id, userId);
        service.removeLike(id, userId);
        log.info("Лайк удален у фильма {} от пользователя {}", id, userId);
    }
}