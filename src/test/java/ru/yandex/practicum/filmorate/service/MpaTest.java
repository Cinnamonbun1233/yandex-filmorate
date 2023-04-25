package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Arrays;
import java.util.Collection;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaTest {
    private final MpaService mpaService;

    @Test
    public void testGetAllMpa() {
        Collection<Mpa> mpaRatingStorage = mpaService.showAllMpas();
        Assertions.assertThat(mpaRatingStorage)
                .isNotEmpty()
                .extracting(Mpa::getName)
                .containsAll(Arrays.asList("G", "PG", "PG-13", "R", "NC-17"));
    }

    @Test
    public void testGetMpaById() {
        Mpa mpa = mpaService.getMpaById(3);
        Assertions.assertThat(mpa)
                .hasFieldOrPropertyWithValue("id", 3)
                .hasFieldOrPropertyWithValue("name", "PG-13");
    }
}