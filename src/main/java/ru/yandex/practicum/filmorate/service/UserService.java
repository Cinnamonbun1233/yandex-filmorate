package ru.yandex.practicum.filmorate.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@Service
@Data
public class UserService {
    private final Map<Integer, User> users = new HashMap<>();
    private int userId = 1;

    public List<User> showAllUsers() {
        return new ArrayList<>(users.values());
    }

    public User createNewUser(User user) {
        validate(user);
        user.setId(userId++);
        users.put(user.getId(), user);
        log.info("Добавлен пользователь с логином '{}'.", user.getLogin());
        return user;
    }

    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new ValidationException("Такого пользователя не существует, необходима регистрация нового пользователя");
        }
        users.remove(user.getId());
        users.put(user.getId(), user);
        log.info("Информация о пользователе '{}' обновлена.", user.getLogin());
        return user;
    }

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