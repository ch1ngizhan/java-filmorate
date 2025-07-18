package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmService {
    Film create(Film film);

    Film update(Film newFilm);

    void delete(Long id);

    Film getFilmById(Long id);

    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    Collection<Film> getPopularFilms(int count);

    Collection<Film> findAll();
}
