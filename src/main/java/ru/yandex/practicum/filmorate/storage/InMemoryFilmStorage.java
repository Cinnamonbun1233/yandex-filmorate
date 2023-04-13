package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.InternalException;
import ru.yandex.practicum.filmorate.exception.DatabaseObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int filmId = 1;

    @Override
    public Map<Integer, Film> getFilms() {
        return films;
    }

    @Override
    public List<Film> showAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film createNewFilm(Film film) {
        checkFilms(film);
        film.setId(filmId++);
        films.put(film.getId(), film);
        log.info("Инфо: фильм '{}' добавлен в коллекцию.", film.getName());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new DatabaseObjectNotFoundException("Ошибка: такого фильма не существует.");
        }
        films.put(film.getId(), film);
        log.info("Инфо: информация о фильме '{}' обновлена.", film.getName());
        return film;
    }

    @Override
    public Film getFilmById(int id) {
        return films.get(id);
    }

    @Override
    public Film deleteFilmById(int id) {
        Film film = films.get(id);
        films.remove(id);
        return film;
    }

    public void checkFilms(Film film) {
        if (check(film)) {
            throw new InternalException("Ошибка: такой фильм уже существует.");
        }
    }

    private boolean check(Film film) {
        return showAllFilms().stream()
                .anyMatch(otherFilm -> otherFilm.getName().equals(film.getName())
                        && otherFilm.getReleaseDate().equals(film.getReleaseDate()));
    }
}