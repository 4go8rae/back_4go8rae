package com.team.project.domain;

import com.team.project.dto.request.ChatMessageDto;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {

    public enum MessageType {
        ENTER, TALK, QUIT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private MessageType type;

    @Column(nullable = false)
    private String roomId;

    @Column(nullable = false)
    private String sender;

    @Column(nullable = false)
    private String message;

    @Builder
    public ChatMessage(final MessageType type, final String roomId, final String sender, final String message) {
        this.type = type;
        this.roomId = roomId;
        this.sender = sender;
        this.message = message;
    }


    public static ChatMessage of(ChatMessageDto chatMessageDto) {
        return ChatMessage.builder()
                .type(chatMessageDto.getType())
                .sender(chatMessageDto.getSender())
                .roomId(chatMessageDto.getRoomId())
                .message(chatMessageDto.getMessage())
                .build();
    }
}
