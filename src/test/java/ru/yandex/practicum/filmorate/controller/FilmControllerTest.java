package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.InternalException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {
    private final static String MOVIE_TEST = "Interstellar";
    private final static long DURATION_TEST = 169L;
    private final static String DESCRIPTION_TEST = "Interstellar is a 2014 epic science fiction film co-written," +
            "directed, and produced by Christopher Nolan.";
    private final static LocalDate RELEASE_TEST = LocalDate.of(2014, 10, 26);
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    FilmService filmService;
    InMemoryFilmStorage filmStorage;
    Film film;
    private Validator validator = factory.getValidator();

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        filmStorage = new InMemoryFilmStorage();
        filmService = new FilmService(filmStorage);
    }

    @Test
    void releaseDateBefore1985() {
        film = new Film(MOVIE_TEST, DESCRIPTION_TEST, LocalDate.of(1894, 1, 1), DURATION_TEST);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    void duplicateFilmTest() {
        film = new Film(MOVIE_TEST, DESCRIPTION_TEST, RELEASE_TEST, DURATION_TEST);
        film.setId(1);
        filmStorage.getFilms().put(film.getId(), film);
        assertThrows(InternalException.class, () -> filmStorage.checkFilms(film));
    }

    @Test
    void emptyNameValidationTest() {
        film = new Film("", DESCRIPTION_TEST, LocalDate.of(1894, 1, 2), 169);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(2);
    }

    @Test
    void blancValidationTest() {
        film = new Film(" ", DESCRIPTION_TEST, LocalDate.of(1894, 1, 2), 169);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(2);
    }

    @Test
    void nullNameValidationTest() {
        film = new Film(null, DESCRIPTION_TEST, LocalDate.of(1894, 1, 2), 169);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(2);
    }

    @Test
    void blancDescriptionTest() {
        film = new Film(MOVIE_TEST, " ", RELEASE_TEST, DURATION_TEST);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    void emptyDescriptionTest() {
        film = new Film(MOVIE_TEST, "", RELEASE_TEST, DURATION_TEST);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    void nullDescriptionTest() {
        film = new Film(MOVIE_TEST, null, RELEASE_TEST, DURATION_TEST);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    void lengthDescriptionAbove200Test() {
        film = new Film(MOVIE_TEST, "Interstellar is a 2014 epic science fiction film co-written, directed,"
                + "and produced by Christopher Nolan. It stars Matthew McConaughey, Anne Hathaway, Jessica Chastain,"
                + "Bill Irwin, Ellen Burstyn, Matt Damon, and Michael Caine. Set in a dystopian future where humanity is"
                + "struggling to survive, the film follows a group of astronauts who travel through a wormhole near Saturn"
                + "in search of a new home for mankind. ", LocalDate.of(1894, 1, 2), 169);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(2);
    }

    @Test
    void durationNotNullTest() {
        film = new Film(MOVIE_TEST, DESCRIPTION_TEST, RELEASE_TEST, 0);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(2);
    }
}