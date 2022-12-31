package com.team.project.dto.request;

import com.team.project.domain.ChatMessage;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ChatMessageDto {

    private ChatMessage.MessageType type;
    private String roomId;
    private String sender;
    private String message;

    @Builder
    public ChatMessageDto(final ChatMessage.MessageType type, final String roomId, final String sender, final String message) {
        this.type = type;
        this.roomId = roomId;
        this.sender = sender;
        this.message = message;
    }
}
