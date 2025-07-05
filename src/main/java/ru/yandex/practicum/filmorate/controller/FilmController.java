package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final InMemoryFilmStorage storage;
    private final FilmService service;


    @GetMapping
    public Collection<Film> findAll() {
        return storage.findAll();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) {
        return storage.getFilmById(id);
    }

    @GetMapping("/popular?count={count}")
    public Collection<Film> getPopularFilms(
            @RequestParam(defaultValue = "10", required = false) int count
    ) {
        return service.getPopularFilms(count);
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        return storage.create(film);
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        return storage.update(newFilm);
    }

    @PutMapping(("/{id}/like/{userId}"))
    public void addLike(@PathVariable Long id,
                        @PathVariable Long userId) {
        service.addLike(id, userId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        storage.delete(id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id,
                           @PathVariable Long userId) {
        service.removeLike(id, userId);
    }
}
