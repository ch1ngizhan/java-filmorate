package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Collection;

public interface MpaService {
    MpaRating getMpaById(int id);

    Collection<MpaRating> getAllMpa();
}
