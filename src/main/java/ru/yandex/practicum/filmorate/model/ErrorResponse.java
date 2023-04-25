package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.Value;

@Data
@Value
public class ErrorResponse {
    String error;
    String description;
}