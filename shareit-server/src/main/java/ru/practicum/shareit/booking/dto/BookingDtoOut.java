package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDtoOut {
    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private ItemDto item;

    private User booker;

    private BookingStatus status;
}
