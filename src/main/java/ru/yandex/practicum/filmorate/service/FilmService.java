package ru.yandex.practicum.filmorate.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Data
public class FilmService {
    protected static final LocalDate DATE = LocalDate.of(1895, 12, 28);
    protected final Map<Integer, Film> films = new HashMap<>();
    protected int filmId = 1;

    public void validate(@Valid @RequestBody Film film) {
        if (film.getReleaseDate().isBefore(DATE) || film.getDuration() < 0) {
            log.warn("Дата выпуска фильма '{}', продолжительность фильма '{}'.", film.getReleaseDate(), film.getDuration());
            throw new ValidationException("Неверная дата выпуска или продолжительность фильма.");
        } else if (film.getName().equals("")) {
            log.warn("Имя фильма пустое '{}'.", film.getName());
            throw new ValidationException("Неверное название фильма.");
        } else if (film.getDescription().length() > 200) {
            log.warn("Описание фильма '{}' слишком длинное.", film.getDescription());
            throw new ValidationException("Неверное описание фильма.");
        }
        Collection<Film> filmCollection = films.values();
        for (Film fl : filmCollection) {
            if (film.getName().equals(fl.getName()) && film.getReleaseDate().equals(fl.getReleaseDate())) {
                log.warn("В базе уже есть фильм с таким названием.");
                throw new ValidationException("Такой фильм уже есть.");
            }
        }
    }
}