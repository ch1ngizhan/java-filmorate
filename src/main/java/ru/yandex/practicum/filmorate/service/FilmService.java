package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private InMemoryFilmStorage filmStorage;
    private InMemoryUserStorage userStorage;


    public void addLike(Long filmId, Long userId) {
        log.info("Пользователь {} пытается поставить лайк фильму {}", userId, filmId);
        filmStorage.validIdFilm(filmId);
        userStorage.validIdUsers(userId);
        Set<Long> likes = filmStorage.getFilmById(filmId).getLikes();
        if (likes.contains(userId)) {
            String errorMessage = String.format("Пользователь %s уже поставил лайк фильму %s", userId, filmId);
            log.warn(errorMessage);
            throw new DuplicatedDataException(errorMessage);
        }
        likes.add(userId);
        int count = likes.size();
        log.debug("Пользователь {} успешно поставил лайк фильму {}.Кол-во лайков : {} ", userId, filmId, count);
        filmStorage.getFilmById(filmId).setLikes(likes);
        filmStorage.getFilmById(filmId).setLikesCount(count);

    }

    // Удаление лайка
    public void removeLike(Long filmId, Long userId) {
        log.info("Попытка удаления лайка: пользователь {} удаляет лайк с фильма {}", userId, filmId);
        filmStorage.validIdFilm(filmId);
        userStorage.validIdUsers(userId);
        Set<Long> likes = filmStorage.getFilmById(filmId).getLikes();
        // Проверяем существование лайка
        if (!likes.contains(userId)) {
            String errorMessage = String.format(
                    "Лайк не найден: пользователь %s не ставил лайк фильму %s",
                    userId, filmId
            );
            log.warn(errorMessage);
            throw new NotFoundException(errorMessage);
        }
        // Удаляем лайк
        likes.remove(userId);
        int count = likes.size();
        log.debug("Лайк успешно удален: пользователь {} убрал лайк с фильма {}.Кол-во лайков : {} ", userId, filmId,
                count);
        filmStorage.getFilmById(filmId).setLikes(likes);
        filmStorage.getFilmById(filmId).setLikesCount(count);
    }

    public Collection<Film> getPopularFilms(int count) {
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt(Film::getLikesCount).reversed())
                .limit(count > 0 ? count : 10)
                .collect(Collectors.toList());
    }


}
