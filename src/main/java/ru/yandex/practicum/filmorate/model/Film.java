package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.ReleaseDateValid;

import java.time.LocalDate;

/**
 * Film.
 */
@Data
@Builder
public class Film {
    private Long id;//целочисленный идентификатор

    @NotBlank(message = "Название не должно быть пустым")
    private String name;//название

    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    private String description;//описание

    @ReleaseDateValid
    private LocalDate releaseDate;//дата релиза

    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private Long duration;//продолжительность фильма

    private Integer likesCount = 0; //Кол-во лайков
}
