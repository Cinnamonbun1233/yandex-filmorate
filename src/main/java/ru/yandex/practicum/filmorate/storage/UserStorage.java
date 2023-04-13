package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {
    Map<Integer, User> getUsers();

    List<User> showAllUsers();

    User createNewUser(User user);

    User updateUser(User user);

    User getUserById(int id);

    User deleteUserById(int id);
}