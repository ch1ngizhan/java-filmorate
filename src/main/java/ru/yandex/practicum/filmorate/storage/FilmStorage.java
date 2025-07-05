package ru.yandex.practicum.filmorate.storage;


import ru.yandex.practicum.filmorate.model.Film;

public interface FilmStorage {

    Film create(Film film);

    Film update(Film newFilm);

    void delete(Long id);
}
