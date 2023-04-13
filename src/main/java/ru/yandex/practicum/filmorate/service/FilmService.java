package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DatabaseObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;

    public List<Film> showAllFilms() {
        log.info("Инфо: список фильмов отправлен.");
        return filmStorage.showAllFilms();
    }

    public Film createNewFilm(Film film) {
        return filmStorage.createNewFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(int id) {
        if (!filmStorage.getFilms().containsKey(id)) {
            throw new DatabaseObjectNotFoundException("Ошибка: фильм не найден.");
        }
        log.info("Инфо: фильм с id '{}' отправлен.", id);
        return filmStorage.getFilmById(id);
    }

    public Film deleteFilmById(int id) {
        if (!filmStorage.getFilms().containsKey(id)) {
            throw new DatabaseObjectNotFoundException("Ошибка: фильм не найден.");
        }
        log.info("Инфо: фильм с id '{}' удален.", id);
        return filmStorage.deleteFilmById(id);
    }

    public Film addLike(int filmId, int userId) {
        if (!filmStorage.getFilms().containsKey(filmId)) {
            throw new DatabaseObjectNotFoundException("Ошибка: фильм не найден.");
        }
        filmStorage.getFilmById(filmId).getUserLikes().add(userId);
        log.info("Инфо: пользователь '{}' поставил лайк фильму '{}'.", userId, filmId);
        return filmStorage.getFilmById(filmId);
    }

    public Film removeLike(int filmId, int userId) {
        if (!filmStorage.getFilms().containsKey(filmId)) {
            throw new DatabaseObjectNotFoundException("Ошибка: фильм не найден.");
        }
        if (!filmStorage.getFilmById(filmId).getUserLikes().contains(userId)) {
            throw new DatabaseObjectNotFoundException("Ошибка: лайк от пользователя отсутствует.");
        }
        filmStorage.getFilmById(filmId).getUserLikes().remove(userId);
        log.info("Инфо: пользователь '{}' удалил лайк к фильму '{}'", userId, filmId);
        return filmStorage.getFilmById(filmId);
    }

    public List<Film> getBestFilms(int count) {
        log.info("Инфо: список популярных фильмов отправлен.");
        return filmStorage.showAllFilms()
                .stream()
                .sorted((film1, film2) -> Integer.compare(film2.getUserLikes().size(), film1.getUserLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}