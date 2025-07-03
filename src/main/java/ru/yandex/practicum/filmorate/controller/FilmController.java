package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final InMemoryFilmStorage storage;


    @GetMapping
    public Collection<Film> findAll() {
        return storage.findAll();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        return storage.create(film);
    }
    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        return storage.update(newFilm);
    }
    @DeleteMapping("/{id}")
    public Optional<Film> delete(@PathVariable Long id) {
        return storage.delete(id);
    }


}
