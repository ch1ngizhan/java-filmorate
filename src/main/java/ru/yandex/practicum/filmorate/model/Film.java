package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

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
    private Long duration;//продолжительность фильма
    private Set<Long> likes; //id тех кто поставил лайк
}
