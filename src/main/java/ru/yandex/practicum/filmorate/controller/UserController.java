package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    final Map<Integer, User> users = new HashMap<>();
    private int userId = 1;

    @GetMapping
    public Collection<User> showAllUsers() {
        return users.values();
    }

    @PostMapping
    public User createNewUser(@Valid @RequestBody User user) {
        validate(user);
        user.setId(userId++);
        users.put(user.getId(), user);
        log.info("Добавлен пользователь с логином '{}'.", user.getLogin());
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        validate(user);
        if (!users.containsKey(user.getId())) {
            throw new ValidationException("Пользователя не существует, необходима регистрация нового пользователя.");
        }
        users.remove(user.getId());
        users.put(user.getId(), user);
        log.info("Информация о пользователе '{}' обновлена.", user.getLogin());
        return user;
    }

    void validate(@Valid @RequestBody User user) {
        if (user.getLogin().contains(" ")) {
            log.warn("Логин пользователя '{}'.", user.getLogin());
            throw new ValidationException("Логин не может быть пустым или содержать пробелы.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        Collection<User> userCollection = users.values();
        for (User us : userCollection) {
            if (user.getLogin().equals(us.getLogin()) || user.getEmail().equals(us.getEmail())) {
                log.warn("E-mail пользователя '{} уже существует.", us);
                throw new ValidationException("Пользователь с таким e-mail уже существует");
            }
        }
    }
}