package com.team.project.dto.request;

import com.team.project.domain.ChatMessage;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatMessageDto {

    private ChatMessage.MessageType type;
    private String roomId;
    private String message;

    private String sender;

    @Builder
    public ChatMessageDto(final ChatMessage.MessageType type, final String roomId, final String message, final String sender) {
        this.type = type;
        this.roomId = roomId;
        this.message = message;
        this.sender = sender;
    }

}
