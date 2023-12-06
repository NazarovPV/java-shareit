package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.Create;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.dto.ItemDtoWithTime;


import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDtoWithTime> getAllItems(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Get ItemDtos");
        return itemService.getAllItemsByOwnerId(ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithTime getItemById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        log.info("Get Item {}", itemId);
        return itemService.getItemById(userId, itemId);
    }

    @PostMapping
    @Validated({Create.class})
    public Item saveNewItem(@RequestHeader("X-Sharer-User-Id") long ownerId, @RequestBody @Valid ItemDto itemDto) {
        log.info("Create ItemDto");
        return itemService.createItem(ownerId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addNewComment(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                    @PathVariable long itemId, @RequestBody CommentDto commentDto) {
        log.info("Create comment User {} item {}", ownerId, itemId);
        return itemService.addNewComment(ownerId, itemId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public Item patchItem(@RequestHeader("X-Sharer-User-Id") long ownerId, @PathVariable long itemId,
                          @RequestBody ItemDto itemDto) {
        log.info("Update ItemDto {}", itemId);
        return itemService.updateItem(ownerId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void removeItem(@RequestHeader("X-Sharer-User-Id") long ownerId, @PathVariable long itemId) {
        log.info("Delete ItemDto {}", itemId);
        itemService.removeItem(ownerId, itemId);
    }

    @GetMapping("/search")
    public List<Item> searchItem(@RequestHeader("X-Sharer-User-Id") long ownerId, @RequestParam String text) {
        log.info("Get Items contains {}", text);
        return itemService.searchItem(ownerId, text);
    }
}