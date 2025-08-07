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
        return storage.findById(id)
                .orElseThrow(() -> new NotFoundException("Жанр с id=" + id + " не найден"));
    }

    @Override
    public Collection<Genre> getAllGenres() {
        return storage.findAll();
    }
}
