package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long>, BookingRepositoryCustom {

    List<Booking> findAllByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findAllByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    List<Booking> findAllByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime start, Sort sort);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findAllByBookerId(Long bookerId, Sort sort);

    List<Booking> findAllByItemId(long itemId);

    @Query(value = "select b.id, b.start_date, b.end_date, b.status, b.booker_id, b.item_id " +
            "from bookings b " +
            "where b.item_id = :itemId and b.start_date < current_time and b.status = 'APPROVED' " +
            "order by b.start_date DESC ", nativeQuery = true)
    List<Booking> findLastBookings(long itemId);

    @Query(value = "select b.id, b.start_date, b.end_date, b.status, b.booker_id, b.item_id " +
            "from bookings b " +
            "where b.item_id = :itemId and b.start_date > current_time and b.status = 'APPROVED' " +
            "order by b.start_date  ", nativeQuery = true)
    List<Booking> findNextBookings(long itemId);

    @Query(value = "select b.id, b.end_date, b.start_date, b.status, b.booker_id, b.item_id " +
            "from bookings b " +
            "join items i on b.item_id = i.id " +
            "where i.owner_id = :ownerId ", nativeQuery = true)
    List<Booking> findByOwnerId(long ownerId);
}