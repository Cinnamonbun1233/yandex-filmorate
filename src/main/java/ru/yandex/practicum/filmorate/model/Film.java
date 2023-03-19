package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.validate.MovieDate;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    @PositiveOrZero
    private int id;
    private Set<Integer> userLikes = new HashSet<>();
    @NotBlank(message = "Ошибка: название фильма неправильное.")
    private final String name;
    @NotBlank(message = "Ошибка: описание фильма отсутствует.")
    @Size(max = 200, message = "Ошибка: описание фильма больше 200 символов.")
    private final String description;
    @NotNull
    @MovieDate
    private final LocalDate releaseDate;
    @Min(value = 1, message = "Ошибка: неправильная продолжительность фильма.")
    @Positive
    private final long duration;
}