package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private final String errorMessage;

    public ErrorResponse(String error) {
        this.errorMessage = error;
    }

}
