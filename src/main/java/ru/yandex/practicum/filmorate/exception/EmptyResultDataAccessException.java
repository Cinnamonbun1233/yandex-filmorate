package ru.yandex.practicum.filmorate.exception;

public class EmptyResultDataAccessException extends RuntimeException {
    public EmptyResultDataAccessException(String message) {
        super(message);
    }
}