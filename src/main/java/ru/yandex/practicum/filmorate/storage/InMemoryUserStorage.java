package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.InternalException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int userId = 1;

    @Override
    public Map<Integer, User> getUsers() {
        return users;
    }

    @Override
    public List<User> showAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User createNewUser(User user) {
        validateUsers(user);
        checkUsers(user);
        user.setId(userId++);
        log.info("Инфо: пользователь с логином '{}' создан.", user.getLogin());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!getUsers().containsKey(user.getId())) {
            throw new ObjectNotFoundException("Ошибка: такого пользователя не существует.");
        }
        validateUsers(user);
        users.put(user.getId(), user);
        log.info("Инфо: информация о пользователе '{}' обновлена.", user.getLogin());
        return user;
    }

    @Override
    public User getUserById(int id) {
        return users.get(id);
    }

    @Override
    public User deleteUserById(int id) {
        User user = users.get(id);
        users.remove(id);
        return user;
    }

    public void validateUsers(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    public void checkUsers(User user) {
        if (showAllUsers().stream().anyMatch(otherUser -> otherUser.getLogin().equals(user.getLogin())
                || otherUser.getEmail().equals(user.getEmail()))) {
            throw new InternalException("Ошибка: пользователь с таким e-mail или логином уже существует");
        }
    }
}