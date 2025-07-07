package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class User {
    @Min(1L)
    private Long id;//целочисленный идентификатор
    @Email
    private String email;//электронная почта
    @NotBlank
    private String login;//логин пользователя
    private String name;//имя для отображения
    @PastOrPresent
    private LocalDate birthday;//дата рождения
    private Set<Long> friends;// id друзей
}
