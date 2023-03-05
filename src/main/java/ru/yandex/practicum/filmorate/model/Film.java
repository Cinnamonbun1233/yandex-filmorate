package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Film {
    @NotBlank
    private final String name;
    @NotBlank
    @Size(max = 200, message = "Описание слишком длинное, больше 200 символов.")
    private final String description;
    @NotNull
    private final LocalDate releaseDate;
    @Min(value = 1)
    private final long duration;
    private int id;
}