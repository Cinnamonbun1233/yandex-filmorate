package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class Film {
    private int id;
    @NotBlank
    private final String name;
    @NotBlank
    @Size(max = 200, message = "Описание фильма слишком длинное, больше 200 символов.")
    private final String description;
    @NotNull
    private final LocalDate releaseDate;
    @Min(value = 1)
    private final long duration;
}