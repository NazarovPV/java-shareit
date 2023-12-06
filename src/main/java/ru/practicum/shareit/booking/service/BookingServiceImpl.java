package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.booking.util.BookingMapper;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingRequestDto addNewBooking(long userId, BookingDto bookingDto) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("Пользователь с таким id не существует");
        }
        Optional<Item> item = itemRepository.findById(bookingDto.getItemId());
        if (item.isPresent()) {

            if (item.get().getOwnerId() == userId) {
                throw new NotFoundException("Бронирование данного предмета по указанному ownerId недоступно");
            }
            if (!item.get().getAvailable()) {
                throw new NotAvailableException("Данный предмет недоступен для бронирования");
            }
        } else {
            throw new NotFoundException("Предлагаемый к бронированию предмет не существует");
        }
        dateValidator(bookingDto.getStart(), bookingDto.getEnd());
        Booking booking = BookingMapper.dtoToBooking(bookingDto);
        booking.setItem(item.get());
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(user.get());
        return BookingMapper.toBookingRequestDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingRequestDto confirmRequest(long userId, long bookingId, Boolean status) {
        BookingStatus bStatus;
        if (status) {
            bStatus = BookingStatus.APPROVED;
        } else {
            bStatus = BookingStatus.REJECTED;
        }
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        Optional<Item> targetItem;

        if (booking.isEmpty()) {
            throw new NotFoundException("Указанного бронирования не найдено");
        }
        if (booking.get().getStatus().equals(BookingStatus.APPROVED)) {
            throw new ValidationException("Бронь уже подтверждена");
        }
        targetItem = itemRepository.findById(booking.get().getItem().getId());
        if (targetItem.isEmpty()) {
            throw new NotFoundException("Указанная вещь не найдена");
        }
        if (targetItem.get().getOwnerId() == userId) {
            booking.get().setStatus(bStatus);
            bookingRepository.save(booking.get());
        } else {
            throw new NotFoundException("Неверный ownerId для изменения статуса данного предмета");
        }
        return BookingMapper.toBookingRequestDto(booking.get());
    }

    @Override
    public BookingRequestDto getBookingById(long userId, long bookingId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isPresent()) {
            Optional<Item> item = itemRepository.findById(booking.get().getItem().getId());
            if (item.get().getOwnerId() == userId || booking.get().getBooker().getId() == userId) {
                return BookingMapper.toBookingRequestDto(booking.get());
            } else {
                throw new NotFoundException("Данный заказ по указанному ownerId не найден");
            }
        }
        throw new NotFoundException("Указанная бронь не найдена");
    }

    @Override
    public List<BookingRequestDto> getByState(long userId, String state) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователя с таким id не существует");
        }
        List<Booking> bookingsByState = new ArrayList<>();
        List<BookingRequestDto> bookingsByStateR = new ArrayList<>();

        switch (state) {
            case "WAITING":
            case "REJECTED":
                bookingsByState = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.valueOf(state),
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case "PAST":
                bookingsByState = bookingRepository.findAllByBookerIdAndEndIsBefore(userId, LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case "CURRENT":
                bookingsByState = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(userId,
                        LocalDateTime.now(), LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"));
                break;
            case "FUTURE":
                bookingsByState = bookingRepository.findAllByBookerIdAndStartIsAfter(userId, LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case "ALL":
                bookingsByState = bookingRepository.findAllByBookerId(userId, Sort.by(Sort.Direction.DESC, "start"));
        }
        for (Booking booking : bookingsByState) {
            bookingsByStateR.add(BookingMapper.toBookingRequestDto(booking));
        }
        return bookingsByStateR;
    }

    @Override
    public List<BookingRequestDto> getByOwner(long ownerId, String state) {
        Optional<User> owner = userRepository.findById(ownerId);
        if (owner.isEmpty()) {
            throw new NotFoundException("Пользователя с таким id не существует");
        }
        List<Booking> bookingsByState = bookingRepository.findByOwnerId(ownerId);
        switch (state) {
            case "WAITING":
            case "REJECTED":
                bookingsByState = bookingRepository.findByOwnerIdAndStatus(ownerId, BookingStatus.valueOf(state));
                break;
            case "PAST":
                bookingsByState = bookingRepository.findByOwnerIdAndEndIsBefore(ownerId);
                break;
            case "CURRENT":
                bookingsByState = bookingRepository.findByOwnerIdAndStartIsBeforeAndEndIsAfter(ownerId);
                break;
            case "FUTURE":
                bookingsByState = bookingRepository.findByOwnerIdAndStartIsAfter(ownerId);
                break;
            case "ALL":
                bookingsByState = bookingRepository.findAllByOwner(ownerId);
        }
        List<BookingRequestDto> bookingsByStateR = new ArrayList<>();
        for (Booking booking : bookingsByState) {
            bookingsByStateR.add(BookingMapper.toBookingRequestDto(booking));
        }
        return bookingsByStateR;
    }

    private void dateValidator(LocalDateTime startTime, LocalDateTime endTime) throws ValidationException {
        if (endTime.isBefore(startTime))
            throw new ValidationException("Время окончания должно быть позже времени начала");
        if (endTime.equals(startTime))
            throw new ValidationException("Время окончания не может быть равно времени начала");

    }
}