package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class MpaServiceImpl implements MpaService {
    private final MpaStorage storage;

    @Override
    public MpaRating getMpaById(int id) {
        return storage.findById(id)
                .orElseThrow(() -> new NotFoundException("MPA с id=" + id + " не найден"));
    }

    @Override
    public Collection<MpaRating> getAllMpa() {
        return storage.findAll();
    }
}
