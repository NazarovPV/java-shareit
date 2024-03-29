package ru.practicum.shareit.booking.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDtoForItem {

    private Long id;

    private Long bookerId;
}
