package ru.yandex.practicum.filmorate.storage;



import ru.yandex.practicum.filmorate.model.Film;

import java.util.Optional;

public interface FilmStorage {

     Film create(Film film);
     Film update(Film newFilm);
     Optional<Film> delete(Long id);
}
