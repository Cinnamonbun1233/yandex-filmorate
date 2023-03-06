package ru.yandex.practicum.filmorate.controller;

import ru.yandex.practicum.filmorate.service.FilmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController extends FilmService {
    @GetMapping
    public List<Film> showAllUsers() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film createNewUser(@Valid @RequestBody Film film) {
        validate(film);
        film.setId(filmId++);
        films.put(film.getId(), film);
        log.info("Фильм '{}' добавлен в коллекцию.", film.getName());
        return film;
    }

    @PutMapping
    public Film updateNewUser(@Valid @RequestBody Film film) {
        validate(film);
        if (!films.containsKey(film.getId())) {
            throw new ValidationException("Такого фильма нет в коллекции.");
        }
        films.remove(film.getId());
        films.put(film.getId(), film);
        log.info("Информация о фильме '{}' обновлена.", film.getName());
        return film;
    }
}