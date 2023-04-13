package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;

public interface FilmStorage {
    Map<Integer, Film> getFilms();

    List<Film> showAllFilms();

    Film createNewFilm(Film film);

    Film updateFilm(Film film);

    Film getFilmById(int id);

    Film deleteFilmById(int id);
}