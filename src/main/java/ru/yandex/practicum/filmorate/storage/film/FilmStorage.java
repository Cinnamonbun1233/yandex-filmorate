package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {

    Collection<Film> showAllFilms();

    Film createNewFilm(Film film);

    Film updateFilm(Film film);

    Film getFilmById(int id);

    void deleteFilmById(int id);

    Film addLike(int filmId, int userId);

    Film removeLike(int filmId, int userId);

    List<Film> getBestFilms(int count);
}