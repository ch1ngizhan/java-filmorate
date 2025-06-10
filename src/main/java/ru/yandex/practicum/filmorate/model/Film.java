package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.Duration;
import java.time.Instant;

/**
 * Film.
 */
@Data
public class Film {
    private Long id;//целочисленный идентификатор
    private String name;//название
    private String description;//описание
    private Instant releaseDate;//дата релиза
    private Duration duration;//продолжительность фильма
}
