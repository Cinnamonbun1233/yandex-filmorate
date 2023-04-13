package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.InternalException;
import ru.yandex.practicum.filmorate.exception.DatabaseObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    public Map<Integer, User> getUsers(){
        return userStorage.getUsers();
    }

    public List<User> showAllUsers() {
        log.info("Инфо: список пользователей отправлен.");
        return userStorage.showAllUsers();
    }

    public User createNewUser(User user) {
        return userStorage.createNewUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public User getUserById(int id) {
        if (!userStorage.getUsers().containsKey(id)) {
            throw new DatabaseObjectNotFoundException("Ошибка: пользователь не найден.");
        }
        return userStorage.getUserById(id);
    }

    public User deleteUserById(int id) {
        if (!userStorage.getUsers().containsKey(id)) {
            throw new DatabaseObjectNotFoundException("Ошибка: пользователь не найден.");
        }
        log.info("Инфо: пользователь с id '{}' удален.", id);
        return userStorage.deleteUserById(id);
    }

    public List<User> addFriendship(int firstId, int secondId) {
        if (!userStorage.getUsers().containsKey(firstId) || !userStorage.getUsers().containsKey(secondId)) {
            throw new DatabaseObjectNotFoundException(String.format("Ошибка: пользователя с id %d или %d не существует.", firstId, secondId));
        }
        if (userStorage.getUserById(firstId).getFriends().contains(secondId)) {
            throw new InternalException("Ошибка: пользователи уже являются друзьями.");
        }
        userStorage.getUserById(firstId).getFriends().add(secondId);
        userStorage.getUserById(secondId).getFriends().add(firstId);
        log.info("Инфо: пользователи '{}' и '{}' теперь друзья.", userStorage.getUserById(firstId).getName(),
                userStorage.getUserById(secondId).getName());
        return Arrays.asList(userStorage.getUserById(firstId), userStorage.getUserById(secondId));
    }

    public List<User> removeFriendship(int firstId, int secondId) {
        if (!userStorage.getUsers().containsKey(firstId) || !userStorage.getUsers().containsKey(secondId)) {
            throw new DatabaseObjectNotFoundException(String.format("Ошибка: пользователя с id %d или %d не существует.", firstId, secondId));
        }
        if (!userStorage.getUserById(firstId).getFriends().contains(secondId)) {
            throw new InternalException("Ошибка: пользователи не являются друзьями.");
        }
        userStorage.getUserById(firstId).getFriends().remove(secondId);
        userStorage.getUserById(secondId).getFriends().remove(firstId);
        log.info("Инфо: пользователи '{}' и '{}' больше не друзья.", userStorage.getUserById(firstId).getName(),
                userStorage.getUserById(secondId).getName());
        return Arrays.asList(userStorage.getUserById(firstId), userStorage.getUserById(secondId));
    }

    public List<User> getFriendsListById(int id) {
        if (!userStorage.getUsers().containsKey(id)) {
            throw new DatabaseObjectNotFoundException("Ошибка: пользователь не найден.");
        }
        log.info("Инфо: запрос получения списка друзей пользователя '{}' выполнен.", userStorage.getUserById(id).getName());
        return userStorage.getUserById(id).getFriends().stream().map(userStorage::getUserById).collect(Collectors.toList());
    }

    public List<User> getCommonFriendsList(int firstId, int secondId) {
        if (!userStorage.getUsers().containsKey(firstId) || !userStorage.getUsers().containsKey(secondId)) {
            throw new DatabaseObjectNotFoundException("Ошибка: пользователи не найдены.");
        }
        User user1 = userStorage.getUserById(firstId);
        User user2 = userStorage.getUserById(secondId);
        log.info("Инфо: список общих друзей '{}' и '{}' отправлен.", user1.getName(), user2.getName());
        return user1.getFriends().stream().filter(friendId -> user2.getFriends().contains(friendId)).
                map(userStorage::getUserById).collect(Collectors.toList());
    }
}