package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController extends UserService {
    @GetMapping
    public List<User> showAllFilms() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User createNewFilm(@Valid @RequestBody User user) {
        validate(user);
        user.setId(userId++);
        users.put(user.getId(), user);
        log.info("Добавлен пользователь с логином '{}'.", user.getLogin());
        return user;
    }

    @PutMapping
    public User updateNewFilm(@Valid @RequestBody User user) {
        validate(user);
        if (!users.containsKey(user.getId())) {
            throw new ValidationException("Такого пользователя не существует, необходима регистрация нового пользователя");
        }
        users.remove(user.getId());
        users.put(user.getId(), user);
        log.info("Информация о пользователе '{}' обновлена.", user.getLogin());
        return user;
    }
}