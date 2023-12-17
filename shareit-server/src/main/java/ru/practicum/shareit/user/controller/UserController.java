package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Slf4j
public class UserController {

    public final UserService userService;

    @PostMapping
    public User createUser(@RequestBody UserDto userDto) {
        log.info("Create user");
        return userService.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public User updateUser(@RequestBody UserDto userDto, @PathVariable Long userId) {
        log.info("Update user {}", userId);
        return userService.updateUser(userDto, userId);
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable Long userId) {
        log.info("Get user {}", userId);
        return userService.getUserById(userId);
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Get all users");
        return userService.getUsers();
    }

    @DeleteMapping("/{userId}")
    public void removeUser(@PathVariable Long userId) {
        log.info("Delete user {}", userId);
        userService.removeUser(userId);
    }
}
