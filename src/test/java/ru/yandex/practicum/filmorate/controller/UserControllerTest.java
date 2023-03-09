package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {
    private final static String TEST_EMAIL = "yandex@yandex.ru";
    private final static String TEST_LOGIN = "Alice";
    private final LocalDate TEST_DATE = LocalDate.of(1992, 10, 18);
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    UserService userService;
    User user;
    private Validator validator = factory.getValidator();

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        userService = new UserService();
    }

    @Test
    void userWithoutNameTest() {
        user = new User(TEST_EMAIL, TEST_LOGIN, TEST_DATE);
        userService.validate(user);
        assertEquals(TEST_LOGIN, user.getName());

        user = new User(TEST_EMAIL, TEST_LOGIN, TEST_DATE);
        user.setName(" ");
        userService.validate(user);
        assertEquals(TEST_LOGIN, user.getLogin());
    }

    @Test
    void duplicateUserTest() {
        user = new User(TEST_EMAIL, TEST_LOGIN, TEST_DATE);
        user.setId(1);
        userService.getUsers().put(user.getId(), user);
        assertThrows(ValidationException.class, () -> userService.validate(user));
    }

    @Test
    void emailBlancTest() {
        user = new User(null, TEST_LOGIN, TEST_DATE);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
        System.out.println(violations);
    }

    @Test
    void emailWithoutATTest() {
        user = new User("null", TEST_LOGIN, TEST_DATE);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
        System.out.println(violations);
    }

    @Test
    void emailNullTest() {
        user = new User(null, TEST_LOGIN, TEST_DATE);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    void loginBlancTest() {
        user = new User(TEST_EMAIL, " ", TEST_DATE);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    void loginNullTest() {
        user = new User(TEST_EMAIL, null, TEST_DATE);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    void birthdateNullTest() {
        user = new User(TEST_EMAIL, TEST_LOGIN, null);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    void birthdateIncorrectTest() {
        user = new User(TEST_EMAIL, TEST_LOGIN, LocalDate.of(2023, 3, 28));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
    }
}