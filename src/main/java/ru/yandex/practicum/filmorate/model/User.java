package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class User {
    @PositiveOrZero
    private int id;
    @NotBlank(message = "Ошибка: e-mail отсутствует.")
    @Email(message = "Ошибка: e-mail неверный.")
    private String email;
    @NotNull(message = "Ошибка: логин отсутствует.")
    @Pattern(regexp = "\\S+", message = "Ошибка: логин содержит пробелы.")
    private String login;
    private String name;
    @NotNull(message = "Ошибка: дата рождения не указана.")
    @PastOrPresent(message = "Ошибка: дата рождения неверная.")
    private LocalDate birthday;
}