package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class BookingRequestDto {
    private final Long id;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final User booker;
    private final Item item;
    private final BookingStatus status;
}
