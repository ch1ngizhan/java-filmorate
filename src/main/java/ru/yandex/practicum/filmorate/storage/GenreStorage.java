package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface GenreStorage {

    Optional<Genre> findById(int id);

    Collection<Genre> findAll();

    Map<Long, Set<Genre>> getGenresForFilms(Collection<Long> filmIds);
}
