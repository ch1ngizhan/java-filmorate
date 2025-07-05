package ru.yandex.practicum.filmorate.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.Set;

@Service
@Slf4j
public class FilmService {
    private InMemoryFilmStorage filmStorage;
    private InMemoryUserStorage userStorage;


    public void addLike(Long filmId,Long userId) {
        log.info("Пользователь {} пытается поставить лайк фильму {}", userId, filmId);
        filmStorage.validIdFilm(filmId);
        userStorage.validIdUsers(userId);
        Set<Long> likes = filmStorage.getFilmById(filmId).getLikes();
        if (likes.contains(userId)) {
            String errorMessage = String.format("Пользователь %s уже поставил лайк фильму %s", userId, filmId);
            log.warn(errorMessage);
            throw new ValidationException(errorMessage);
        }
        likes.add(userId);
        log.debug("Пользователь {} успешно поставил лайк фильму {}", userId, filmId);
        filmStorage.getFilmById(filmId).setLikes(likes);

    }


}
