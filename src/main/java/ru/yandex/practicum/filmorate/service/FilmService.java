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


    public void addLike(Long id,Long userId) {
        log.info("");
        filmStorage.validIdFilm(id);
        userStorage.validIdUsers(userId);
        Set<Long> likes = filmStorage.getFilmById(id).getLikes();
        if (likes.contains(userId)) {
            throw new ValidationException("Пользователь уже поставил лайк этому фильму");
        }
        likes.add(userId);
        log.debug("");

    }


}
