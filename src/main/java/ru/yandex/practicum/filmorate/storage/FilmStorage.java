package ru.yandex.practicum.filmorate.storage;


import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    Film create(Film film);

    Film update(Film newFilm);

    void delete(Long id);

    Optional<Film> getFilmById(Long id);

    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    Collection<Film> getPopularFilms(int count);

    Collection<Film> findAll();
}
