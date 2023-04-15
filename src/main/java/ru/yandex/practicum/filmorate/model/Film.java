package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.validate.MovieDate;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class Film {
    @PositiveOrZero
    private int id;
    @NotBlank(message = "Ошибка: название фильма неправильное.")
    private String name;
    @NotNull(message = "Ошибка: описание фильма отсутствует.")
    @Size(max = 200, message = "Ошибка: описание фильма больше 200 символов.")
    private String description;
    @NotNull
    @MovieDate
    private LocalDate releaseDate;
    @Min(value = 1, message = "Ошибка: неправильная продолжительность фильма.")
    @Positive
    private long duration;
    private Mpa mpa;
    private List<Genre> genres;
}