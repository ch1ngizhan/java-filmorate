package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreStorage storage;

    @Override
    public Genre getGenreById(int id) {
        log.info("Запрос жанра по ID: {}", id);
        Genre genre = storage.findById(id)
                .orElseThrow(() -> {
                    log.error("Жанр с id={} не найден", id);
                    return new NotFoundException("Жанр с id=" + id + " не найден");
                });
        log.info("Найден жанр: {}", genre);
        return genre;
    }

    @Override
    public Collection<Genre> getAllGenres() {
        log.info("Запрос на получение всех жанров");
        Collection<Genre> genres = storage.findAll();
        log.info("Найдено жанров: {}", genres.size());
        return genres;
    }
}
