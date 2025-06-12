package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;

/**
 * Film.
 */
@Data
@Builder
public class Film {
    private Long id;//целочисленный идентификатор
    private String name;//название
    private String description;//описание
    private LocalDate releaseDate;//дата релиза
    private Duration duration;//продолжительность фильма
}
