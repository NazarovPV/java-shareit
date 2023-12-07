package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.Create;
import ru.practicum.shareit.user.dto.Update;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Get users");
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public Optional<User> getUserById(@PathVariable long userId) {
        log.info("Get user {}", userId);
        return userService.getUserById(userId);
    }

    @PostMapping
    @Validated({Create.class})
    public User saveNewUser(@RequestBody @Valid UserDto userDto) {
        log.info("Create user");
        return userService.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    @Validated({Update.class})
    public User patchUser(@PathVariable long userId, @RequestBody @Valid UserDto userDto) {
        log.info("Update user {}", userId);
        return userService.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void removeUser(@PathVariable long userId) {
        log.info("Delete user {}", userId);
        userService.removeUser(userId);
    }
}