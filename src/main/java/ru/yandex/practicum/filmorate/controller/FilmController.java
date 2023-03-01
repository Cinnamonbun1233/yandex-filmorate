package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private static final LocalDate DATE = LocalDate.of(1895, 12, 28);
    final Map<Integer, Film> films = new HashMap<>();
    private int filmId = 1;

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        validate(film);
        film.setId(filmId++);
        films.put(film.getId(), film);
        log.info("Фильм '{}' добавлен в коллекцию", film.getName());
        return film;
    }

    @PutMapping
    public Film put(@Valid @RequestBody Film film) {
        validate(film);
        if (!films.containsKey(film.getId())) {
            throw new ValidationException("Такого фильма нет");
        }
        films.remove(film.getId());
        films.put(film.getId(), film);
        log.info("Информация о фильме '{}' обновлена", film.getName());
        return film;
    }

    void validate(@Valid @RequestBody Film film) {
        if (film.getReleaseDate().isBefore(DATE) || film.getDuration() < 0) {
            log.warn("Дата выпуска фильма '{}'\n Продолжительность фильма '{}'", film.getReleaseDate(), film.getDuration());
            throw new ValidationException("Неверная дата выпуска фильма или продолжительность");
        } else if (film.getName().equals("")) {
            log.warn("Имя фильма пустое '{}", film.getName());
            throw new ValidationException("Неверное название фильма");
        } else if (film.getDescription().length() > 200) {
            log.warn("Описание фильма '{}' слишком длинное", film.getDescription());
            throw new ValidationException("Неверное описание фильма");
        }
        Collection<Film> filmCollection = films.values();
        for (Film fl : filmCollection) {
            if (film.getName().equals(fl.getName()) && film.getReleaseDate().equals(fl.getReleaseDate())) {
                log.warn("В базе уже есть фильм с таким названием");
                throw new ValidationException("Такой фильм уже есть");
            }
        }
    }
}