package com.team.project.dto.response;

import com.team.project.domain.ChatMessage;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatMessageResponseDto {

    private ChatMessage.MessageType type;
    private String roomId;
    private String message;
    private String sender;

    @Builder
    public ChatMessageResponseDto(final ChatMessage.MessageType type, final String roomId, final String message, final String sender) {
        this.type = type;
        this.roomId = roomId;
        this.message = message;
        this.sender = sender;
    }
}
