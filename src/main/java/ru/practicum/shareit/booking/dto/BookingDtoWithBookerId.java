package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Transient;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDtoWithBookerId {
    private Long id;
    private Long bookerId;

    @Transient
    private LocalDateTime start;

    @Transient
    private LocalDateTime end;
}