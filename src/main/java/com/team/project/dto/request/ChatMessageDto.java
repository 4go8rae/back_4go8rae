package com.team.project.dto.request;

import com.team.project.domain.ChatMessage;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
public class ChatMessageDto {

    private final ChatMessage.MessageType type;
    private final String roomId;
    private final String sender;
    private final String message;


    @Builder
    public ChatMessageDto(final ChatMessage.MessageType type, final String roomId, final String sender, final String message) {
        this.type = type;
        this.roomId = roomId;
        this.sender = sender;
        this.message = message;
    }

    public ChatMessage toEntity() {
        return ChatMessage.builder()
                .type(type)
                .roomId(roomId)
                .sender(sender)
                .message(message)
                .build();
    }
}
