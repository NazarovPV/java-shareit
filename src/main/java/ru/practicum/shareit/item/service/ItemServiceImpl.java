package ru.practicum.shareit.item.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoWithBookerId;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.booking.util.BookingMapper;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithTime;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.QItem;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.item.util.CommentMapper;
import ru.practicum.shareit.item.util.ItemMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor

public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public Item createItem(long ownerId, ItemDto itemDto) {
        Optional<User> owner = userRepository.findById(ownerId);
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Поле available должно быть true");
        }
        if (owner.isEmpty()) {
            throw new NotFoundException("Передан неверный ownerId");
        }
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ValidationException("Ошибка в передаче значения поля name");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ValidationException("Ошибка в передаче значения поля description");
        }
        Item item = ItemMapper.dtoToItem(itemDto);
        item.setOwnerId(owner.get().getId());
        return itemRepository.save(item);
    }

    @Override
    public List<ItemDtoWithTime> getAllItemsByOwnerId(long ownerId) {
        BooleanExpression byOwnerId = QItem.item.ownerId.eq(ownerId);
        Iterable<Item> iterableItemsList = itemRepository.findAll(byOwnerId);
        List<Item> itemsList = new ArrayList<>();
        iterableItemsList.forEach(itemsList::add);

        List<ItemDtoWithTime> itemsDtoList = new ArrayList<>();
        for (var item : itemsList) {
            List<Booking> bookings = bookingRepository.findAllByItemId(item.getId());
            ItemDtoWithTime itemDtoWithTime;
            BookingDtoWithBookerId lastBookingTimeDto = null;
            BookingDtoWithBookerId nextBookingTimeDto = null;
            for (var booking : bookings) {
                if (Objects.equals(booking.getItem().getId(), item.getId())) {
                    List<Booking> lastBooking = bookingRepository.findLastBookings(item.getId());
                    List<Booking> nextBooking = bookingRepository.findNextBookings(item.getId());
                    if (!lastBooking.isEmpty()) {
                        lastBookingTimeDto = BookingMapper.toBookingDtoWithBookerId(lastBooking.get(0));
                    }
                    if (!nextBooking.isEmpty()) {
                        nextBookingTimeDto = BookingMapper.toBookingDtoWithBookerId(nextBooking.get(0));
                    }
                }
            }
            itemDtoWithTime = ItemMapper.itemToItemDtoWithTime(item, lastBookingTimeDto, nextBookingTimeDto);
            List<Comment> comments = commentRepository.findByItemId(item.getId());
            List<CommentDto> commentDtos = new ArrayList<>();
            comments.forEach(e -> commentDtos.add(CommentMapper.toCommentDto(e)));
            itemDtoWithTime.setComments(commentDtos);

            itemsDtoList.add(itemDtoWithTime);
        }

        boolean isAnotherBookingsNotExist = itemsDtoList.stream().allMatch(e -> e.getNextBooking() == null && e.getLastBooking() == null);
        if (isAnotherBookingsNotExist) {
            return itemsDtoList;
        }
        return itemsDtoList.stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
    }

    @Override
    public ItemDtoWithTime getItemById(long userId, long itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            throw new NotFoundException("Предмет не найден");
        }

        ItemDtoWithTime itemDtoWithTime;
        BookingDtoWithBookerId lastBookingTimeDto;
        BookingDtoWithBookerId nextBookingTimeDto;
        if (item.get().getOwnerId() == userId) {

            lastBookingTimeDto = bookingRepository.findLastBookings(item.get().getId()).stream()
                    .findFirst()
                    .map(BookingMapper::toBookingDtoWithBookerId)
                    .orElse(null);
            nextBookingTimeDto = bookingRepository.findNextBookings(item.get().getId()).stream()
                    .findFirst()
                    .map(BookingMapper::toBookingDtoWithBookerId)
                    .orElse(null);

            itemDtoWithTime = ItemMapper.itemToItemDtoWithTime(item.get(), lastBookingTimeDto, nextBookingTimeDto);
        } else {
            itemDtoWithTime = ItemMapper.itemToItemDtoWithTime(item.get(), null, null);
        }
        List<Comment> comments = commentRepository.findByItemId(item.get().getId());
        List<CommentDto> commentDtos = new ArrayList<>();
        comments.forEach(e -> commentDtos.add(CommentMapper.toCommentDto(e)));
        itemDtoWithTime.setComments(commentDtos);

        return itemDtoWithTime;
    }

    @Transactional
    @Override
    public Item updateItem(long ownerId, long itemId, ItemDto itemDto) {
        return itemRepository.updateById(ownerId, itemId, ItemMapper.dtoToItem(itemDto));
    }

    @Transactional
    @Override
    public void removeItem(long ownerId, long itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isPresent()) {
            if (item.get().getOwnerId() == ownerId) {
                itemRepository.deleteById(itemId);
            } else {
                throw new ForbiddenException("Доступ на изменение по указанному ownerId запрещен");
            }
        } else {
            throw new NotFoundException("Вещь не найдена");
        }
    }

    @Override
    public List<Item> searchItem(long ownerId, String text) {
        if (text.isBlank()) {
            return Collections.EMPTY_LIST;
        }

        return itemRepository.search(text);
    }

    @Transactional
    @Override
    public CommentDto addNewComment(long userId, long itemId, CommentDto commentDto) {
        List<Booking> bookings = bookingRepository.findAllByBookerId(userId, Sort.by(Sort.Direction.DESC, "start"));
        Optional<User> user = userRepository.findById(userId);
        Optional<Item> item = itemRepository.findById(itemId);
        LocalDateTime now = LocalDateTime.now();
        String text;
        if (commentDto.getText().isBlank()) {
            throw new ValidationException("Невозможно добавить пустой отзыв");
        } else {
            text = commentDto.getText();
        }
        CommentDto newCommentDto = null;
        if (item.isPresent() && user.isPresent()) {
            for (var booking : bookings) {
                if (booking.getBooker().getId() == userId && booking.getItem().getId() == itemId && booking.getEnd().isBefore(now)) {
                    Comment comment = Comment.builder().text(text).authorName(user.get().getName()).itemId(itemId)
                            .created(now).build();
                    CommentMapper.toCommentDto(commentRepository.save(comment));
                    newCommentDto = CommentMapper.toCommentDto(comment);
                    break;
                }
            }
            if (newCommentDto == null) {
                throw new ValidationException("Пользователь с данным userId не может оставить отзыв на данный предмет");
            }
        } else {
            throw new NotFoundException("Предмет или пользователь не найден");
        }
        return newCommentDto;

    }
}