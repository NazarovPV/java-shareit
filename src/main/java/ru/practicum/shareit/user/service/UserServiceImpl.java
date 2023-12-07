package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.user.util.EmailValidator;
import ru.practicum.shareit.user.util.UserMapper;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return user;
        } else {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    @Override
    @Transactional
    public User createUser(UserDto userDto) {
        if (isEmailExist(userDto.getEmail())) {
            userRepository.save(UserMapper.toUser(userDto));
            throw new ConflictException("Такой email уже существует");
        }
        if (userDto.getEmail() == null) {
            throw new ValidationException("Не указан email");
        }
        if (!EmailValidator.isValid(userDto.getEmail())) {
            throw new ValidationException("Указан неверный email");
        }
        return userRepository.save(UserMapper.toUser(userDto));
    }

    @Transactional
    @Override
    public User updateUser(long userId, UserDto userDto) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            if (isEmailExist(userDto.getEmail()) && !Objects.equals(user.getEmail(), userDto.getEmail())) {
                throw new ConflictException("Такой email уже существует");
            }
        }
        return userRepository.updateById(userId, UserMapper.toUser(userDto));
    }

    @Transactional
    @Override
    public void removeUser(long userId) {
        userRepository.deleteById(userId);
    }

    private boolean isEmailExist(String email) {
        List<User> users = userRepository.findAll();
        return users.stream().anyMatch(e -> e.getEmail().equalsIgnoreCase(email));
    }
}