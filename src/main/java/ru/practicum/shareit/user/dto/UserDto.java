package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;
    @NotEmpty
    private String name;
    @Email
    @NotBlank
    private String email;
}