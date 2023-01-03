package com.team.project.domain;

import com.team.project.dto.request.ChatMessageDto;
import lombok.*;
import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ChatMessage {

    public enum MessageType {
        ENTER, TALK, QUIT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType type;

    @Column(nullable = false)
    private String roomId;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private String sender;

    @Builder
    public ChatMessage(final MessageType type, final String roomId, final String message, final String sender) {
        this.type = type;
        this.roomId = roomId;
        this.message = message;
        this.sender = sender;
    }

    @Builder
    public ChatMessage(ChatMessageDto chatMessageDto) {
        this.type = chatMessageDto.getType();
        this.roomId = chatMessageDto.getRoomId();
        this.message = chatMessageDto.getMessage();
        this.sender = chatMessageDto.getSender();
    }
}
