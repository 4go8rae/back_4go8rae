package com.team.project.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class ChatRoomDto {

    private String roomId;
    private String name;

    @Builder
    public ChatRoomDto(final String roomId, final String name) {
        this.roomId = roomId;
        this.name = name;
    }

    public static ChatRoomDto create(String name) {
        ChatRoomDto room = new ChatRoomDto();
        room.roomId = UUID.randomUUID().toString();
        room.name = name;
        return room;
    }
}
