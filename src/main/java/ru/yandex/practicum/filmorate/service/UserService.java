package ru.yandex.practicum.filmorate.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Data
public class UserService {
    protected final Map<Integer, User> users = new HashMap<>();
    protected int userId = 1;

    public void validate(@Valid @RequestBody User user) {
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
                log.warn("E-mail пользователя '{}' уже существует.", us);
                throw new ValidationException("Пользователь с таким e-mail уже существует.");
            }
        }
    }
}