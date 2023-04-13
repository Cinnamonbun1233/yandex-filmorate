package ru.yandex.practicum.filmorate.validate;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

@Target(FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MovieDateValidator.class)
public @interface MovieDate {
    String message() default "Ошибка: дата релиза фильма раньше появления первого фильма.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}