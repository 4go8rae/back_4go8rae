package com.team.project.domain;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id")
    private ChatRoom chatRoom;

    @Builder
    public ChatMessage(final MessageType type, final String roomId, final String sender, final String message, final ChatRoom chatRoom) {
        this.type = type;
        this.roomId = roomId;
        this.sender = sender;
        this.message = message;
        this.chatRoom = chatRoom;
    }

    public static ChatMessage createChatMessage(ChatRoom chatRoom, MessageType type, String sender, String roomId, String message) {
        return ChatMessage.builder()
                .chatRoom(chatRoom)
                .type(type)
                .sender(sender)
                .roomId(roomId)
                .message(message)
                .build();
    }
}
