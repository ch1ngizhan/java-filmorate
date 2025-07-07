package ru.yandex.practicum.filmorate.model;


import lombok.Getter;

@Getter
public class ErrorResponse {
    private final String error;
    private final String description;

    public ErrorResponse(String description) {
        this.error = "Ошибка";
        this.description = description;
    }
}
