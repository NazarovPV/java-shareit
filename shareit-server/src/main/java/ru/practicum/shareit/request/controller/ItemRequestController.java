package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Slf4j
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto createItemRequest(@Valid @RequestBody ItemRequestDtoIn itemRequestDtoIn,
                                            @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Create item request by user{}", userId);
        return itemRequestService.createItemRequest(itemRequestDtoIn, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getAllByRequestorId(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Get all requests");
        return itemRequestService.getAllByRequestorId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllByRequestsByUserId(@RequestHeader(USER_ID_HEADER) Long userId,
                                                         @Min(0) @RequestParam(defaultValue = "0") int from,
                                                         @Min(0) @RequestParam(defaultValue = "10") int size) {
        log.info("Get all requests by user{}", userId);
        return itemRequestService.getAllByNotRequestorId(userId, from, size);
    }

    @GetMapping("{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader(USER_ID_HEADER) Long userId,
                                         @PathVariable Long requestId) {
        log.info("Get request by requestId{}", requestId);
        return itemRequestService.getRequestById(userId, requestId);
    }

}
