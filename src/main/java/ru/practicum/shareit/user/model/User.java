package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
@Getter
public class User {
    private long id;
    private String name;
    private String email;
}