package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.UnsupportedStatusException;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingRequestDto addNewBooking(@RequestHeader("X-Sharer-User-Id") long ownerId, @RequestBody @Valid BookingDto bookingDto) {
        log.info("Create BookingDto User {} ", ownerId);
        return bookingService.addNewBooking(ownerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingRequestDto confirmRequest(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long bookingId,
                                  @RequestParam(name = "approved") Boolean status) {
        log.info("Confirming request Booking{} }User {} ",bookingId, userId);
            return bookingService.confirmRequest(userId, bookingId, status);
        }

    @GetMapping("/{bookingId}")
    public BookingRequestDto getBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                               @PathVariable long bookingId) {
        log.info("Get BookingDto {} user {} ", bookingId, userId);
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingRequestDto> getAllByState(@RequestHeader("X-Sharer-User-Id") long userId, @RequestParam(name = "state", defaultValue = "ALL") String state) {
        if (isBookingState(state)) {
            return bookingService.getByState(userId, state);
        }
        log.info("Get all by state {} user {} ", state, userId);
        throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
    }

    @GetMapping("/owner")
    public List<BookingRequestDto> getOwnersBookings(@RequestHeader("X-Sharer-User-Id") long ownerId, @RequestParam(name = "state", defaultValue = "ALL") String state) {
        if (isBookingState(state)) {
            return bookingService.getByOwner(ownerId, state);
        }
        log.info("Get BookingDtos Owner {} state {}", ownerId, state);
        throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
    }

    private boolean isBookingState(String state) {
        return Arrays.stream(BookingState.values()).anyMatch((s) -> s.name().equals(state));
    }
}