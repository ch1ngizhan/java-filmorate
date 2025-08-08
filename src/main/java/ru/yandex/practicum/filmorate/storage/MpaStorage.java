package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Collection;
import java.util.Optional;

public interface MpaStorage {
    Optional<MpaRating> findById(int id);

    Collection<MpaRating> findAll();
}
