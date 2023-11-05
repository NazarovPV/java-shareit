package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;

@Data
@AllArgsConstructor
@Builder
public class ItemDto {
    private String name;
    private String description;
    private Boolean available;
    private Long ownerId;
    private ItemRequest request;
}