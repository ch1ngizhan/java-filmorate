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
        log.info("Запрос MPA по ID: {}", id);
        MpaRating mpa = storage.findById(id)
                .orElseThrow(() -> {
                    log.error("MPA с id={} не найден", id);
                    return new NotFoundException("MPA с id=" + id + " не найден");
                });
        log.info("Найден MPA: {}", mpa);
        return mpa;
    }

    @Override
    public Collection<MpaRating> getAllMpa() {
        log.info("Запрос на получение всех MPA");
        Collection<MpaRating> mpaRatings = storage.findAll();
        log.info("Найдено MPA рейтингов: {}", mpaRatings.size());
        return mpaRatings;
    }
}
