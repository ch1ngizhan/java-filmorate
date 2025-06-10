package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.Instant;

@Data
public class User {
    private Long id;//целочисленный идентификатор
    private String email;//электронная почта
    private String login;//логин пользователя
    private String name;//имя для отображения
    private Instant birthday;//дата рождения
}
