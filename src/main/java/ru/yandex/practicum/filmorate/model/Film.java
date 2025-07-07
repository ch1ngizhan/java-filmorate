package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
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
    @Min(1L)
    private Long id;//целочисленный идентификатор
    @NotBlank
    private String name;//название
    @Size(max = 200)
    private String description;//описание
    private LocalDate releaseDate;//дата релиза
    @Positive
    private Long duration;//продолжительность фильма
    private Set<Long> likes;
    @PositiveOrZero//id тех кто поставил лайк
    private Integer likesCount; //Кол-во лайков
}
