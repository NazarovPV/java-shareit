package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto,
                              @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Create ItemDto");
        return itemService.createItem(itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoBooking getItemById(@PathVariable Long itemId,
                                      @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Get Item {}", itemId);
        return itemService.getItemById(itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @PathVariable Long itemId,
                              @RequestHeader(USER_ID_HEADER) Long userId) {

        itemDto.setId(itemId);
        log.info("Update ItemDto {}", itemId);
        return itemService.updateItem(itemDto, userId);
    }

    @GetMapping
    public List<ItemDtoBooking> getItemsByUserId(@RequestHeader(USER_ID_HEADER) Long userId,
                                                 @Min(0) @RequestParam(defaultValue = "0") int from,
                                                 @Min(0) @RequestParam(defaultValue = "10") int size) {
        log.info("Get ItemDtos");
        return itemService.getItemsByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsByQuery(@RequestParam("text") String query,
                                         @Min(0) @RequestParam(defaultValue = "0") int from,
                                         @Min(0) @RequestParam(defaultValue = "10") int size) {
        log.info("Get Items contains {}", query);
        return itemService.getItemsByQuery(query, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@Valid @RequestBody Comment comment,
                                    @PathVariable("itemId") Long itemId,
                                    @RequestHeader(USER_ID_HEADER) Long bookerId) {
        log.info("Create comment User {} item {}", bookerId, itemId);
        return itemService.createComment(comment, itemId, bookerId);
    }
}
