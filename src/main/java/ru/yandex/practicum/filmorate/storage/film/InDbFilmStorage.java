package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EmptyResultDataAccessException;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class InDbFilmStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Film> showAllFilms() {
        final String sqlQuery = "SELECT * FROM films";
        return jdbcTemplate.query(sqlQuery, this::makeFilm);
    }

    @Override
    public Film createNewFilm(Film film) {
        final String sqlQuery = "INSERT INTO films (name, description, release_date, duration) " +
                "VALUES (?, ?, ?, ?)";
        KeyHolder generatedId = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setLong(4, film.getDuration());
            return stmt;
        }, generatedId);

        film.setId(Objects.requireNonNull(generatedId.getKey()).intValue());
        final String mpaSqlQuery = "INSERT INTO mpa_films (film_id, mpa_id) VALUES (?, ?)";
        jdbcTemplate.update(mpaSqlQuery, film.getId(), film.getMpa().getId());
        final String genresSqlQuery = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";

        if (film.getGenres() != null) {
            for (Genre g : film.getGenres()) {
                jdbcTemplate.update(genresSqlQuery, film.getId(), g.getId());
            }
        }

        film.setMpa(findMpa(film.getId()));
        film.setGenres(findGenres(film.getId()));
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        final String checkQuery = "SELECT * FROM films WHERE id = ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(checkQuery, film.getId());

        if (!filmRows.next()) {
            log.warn("Фильм с id '{}' не найден.", film.getId());
            throw new EntityNotFoundException("Фильм не найден.");
        }

        final String sqlQuery = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
                "duration = ? " +
                "WHERE id = ?";

        if (film.getMpa() != null) {
            final String deleteMpa = "DELETE FROM mpa_films WHERE film_id = ?";
            final String updateMpa = "INSERT INTO mpa_films (film_id, mpa_id) VALUES (?, ?)";

            jdbcTemplate.update(deleteMpa, film.getId());
            jdbcTemplate.update(updateMpa, film.getId(), film.getMpa().getId());
        }

        if (film.getGenres() != null) {
            final String deleteGenresQuery = "DELETE FROM film_genre WHERE film_id = ?";
            final String updateGenresQuery = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";

            jdbcTemplate.update(deleteGenresQuery, film.getId());
            for (Genre g : film.getGenres()) {
                String checkDuplicate = "SELECT * FROM film_genre WHERE film_id = ? AND genre_id = ?";
                SqlRowSet checkRows = jdbcTemplate.queryForRowSet(checkDuplicate, film.getId(), g.getId());
                if (!checkRows.next()) {
                    jdbcTemplate.update(updateGenresQuery, film.getId(), g.getId());
                }
            }
        }

        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getId());
        film.setMpa(findMpa(film.getId()));
        film.setGenres(findGenres(film.getId()));
        return film;
    }

    @Override
    public Film getFilmById(int id) {
        final String sqlQuery = "SELECT * FROM films WHERE id = ?";
        return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, this::makeFilm, id))
                .orElseThrow(() -> new EmptyResultDataAccessException("Фильм не найден"));
    }

    @Override
    public void deleteFilmById(int id) {
        final String genresSqlQuery = "DELETE FROM film_genre WHERE film_id = ?";
        String mpaSqlQuery = "DELETE FROM mpa_films WHERE film_id = ?";

        jdbcTemplate.update(genresSqlQuery, id);
        jdbcTemplate.update(mpaSqlQuery, id);
        final String sqlQuery = "DELETE FROM films WHERE id = ?";

        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public Film addLike(int filmId, int userId) {
        validate(filmId, userId);
        final String sqlQuery = "INSERT INTO films_likes (film_id, user_id) VALUES (?, ?)";

        jdbcTemplate.update(sqlQuery, filmId, userId);
        return getFilmById(filmId);
    }

    @Override
    public Film removeLike(int filmId, int userId) {
        validate(filmId, userId);
        final String sqlQuery = "DELETE FROM films_likes WHERE film_id = ? AND user_id = ?";

        jdbcTemplate.update(sqlQuery, filmId, userId);
        log.info("Пользователь '{}' удалил лайк к фильму '{}'.", userId, filmId);
        return getFilmById(filmId);
    }

    @Override
    public List<Film> getBestFilms(int count) {
        String sqlQuery = "SELECT id, name, description, release_date, duration " +
                "FROM films " +
                "LEFT JOIN films_likes AS fl ON films.id=fl.film_id " +
                "GROUP BY films.id, fl.film_id IN " +
                "(SELECT film_id FROM films_likes) " +
                "ORDER BY COUNT(fl.film_id) DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::makeFilm, count);
    }

    private Film makeFilm(ResultSet resultSet, int rowNum) throws SQLException {
        final int id = resultSet.getInt("id");
        final String name = resultSet.getString("name");
        final String description = resultSet.getString("description");
        final LocalDate releaseDate = resultSet.getDate("release_date").toLocalDate();
        long duration = resultSet.getLong("duration");
        return new Film(id, name, description, releaseDate, duration, findMpa(id), findGenres(id));
    }

    private List<Genre> findGenres(int filmId) {
        final String genresSqlQuery = "SELECT genre.genre_id, name " +
                "FROM genre " +
                "LEFT JOIN film_genre AS fg ON genre.genre_id = fg.genre_id " +
                "WHERE film_id = ?";
        return jdbcTemplate.query(genresSqlQuery, this::makeGenre, filmId);
    }

    private Mpa findMpa(int filmId) {
        final String mpaSqlQuery = "SELECT id, name " +
                "FROM mpa " +
                "LEFT JOIN mpa_films AS mf ON mpa.id = mf.mpa_id " +
                "WHERE film_id = ?";
        return jdbcTemplate.queryForObject(mpaSqlQuery, this::makeMpa, filmId);
    }

    private Genre makeGenre(ResultSet resultSet, int rowNum) throws SQLException {
        final int id = resultSet.getInt("genre_id");
        final String name = resultSet.getString("name");
        return new Genre(id, name);
    }

    private Mpa makeMpa(ResultSet resultSet, int rowNum) throws SQLException {
        final int id = resultSet.getInt("id");
        final String name = resultSet.getString("name");
        return new Mpa(id, name);
    }

    private void validate(int filmId, int userId) {
        final String checkFilmQuery = "SELECT * FROM films WHERE id = ?";
        final String checkUserQuery = "SELECT * FROM users WHERE id = ?";

        SqlRowSet filmsRows = jdbcTemplate.queryForRowSet(checkFilmQuery, filmId);
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(checkUserQuery, userId);

        if (!filmsRows.next() || !userRows.next()) {
            log.warn("Фильм '{}' и(или) пользователь '{}' не найден.", filmId, userId);
            throw new EntityNotFoundException("Фильм или пользователь не найдены.");
        }
    }
}