package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class User {
    private Long id;//целочисленный идентификатор
    private String email;//электронная почта
    private String login;//логин пользователя
    private String name;//имя для отображения
    private LocalDate birthday;//дата рождения
    private Set<Long> friends;// id друзей
}
