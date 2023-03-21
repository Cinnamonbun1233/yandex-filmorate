package ru.yandex.practicum.filmorate.exception;

public class DatabaseObjectNotFoundException extends RuntimeException {
    public DatabaseObjectNotFoundException(String message) {
        super(message);
    }
}