package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    @PositiveOrZero
    private int id;
    private Set<Integer> friends = new HashSet<>();
    @NotBlank(message = "Ошибка: e-mail отсутствует.")
    @Email(message = "Ошибка: e-mail неверный.")
    @Email
    private final String email;
    @NotNull(message = "Ошибка: логин отсутствует.")
    @Pattern(regexp = "\\S+", message = "Ошибка: логин содержит пробелы.")
    private final String login;
    @NotNull(message = "Ошибка: дата рождения не указана")
    @PastOrPresent(message = "Ошибка: дата рождения неверная")
    private final LocalDate birthday;
    private String name;
}