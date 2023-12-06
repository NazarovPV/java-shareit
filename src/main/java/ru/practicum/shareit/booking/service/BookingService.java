package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.util.List;

public interface BookingService {
    BookingRequestDto addNewBooking(long ownerId, BookingDto bookingDto);

    BookingRequestDto confirmRequest(long ownerId, long bookingId, Boolean status);

    BookingRequestDto getBookingById(long userId, long bookingId);

    List<BookingRequestDto> getByState(long userId, String state);

    List<BookingRequestDto> getByOwner(long ownerId, String state);
}