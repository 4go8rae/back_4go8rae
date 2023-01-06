package com.team.project.dto.request;

import com.team.project.domain.ChatMessage;
import com.team.project.domain.ChatRoom;
import com.team.project.domain.Member;
import com.team.project.dto.response.MemberResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


public class ChatMessageDto {
    @Getter
    @Builder
    public static class Send {
        private String message;
        private Long senderId;
        private Long receiverId;
        private Long roomId;

        public ChatMessage toMessage() {
            return ChatMessage.builder()
                    .message(message)
                    .sender(Member.builder().id(senderId).build())
                    .receiver(Member.builder().id(receiverId).build())
                    .chatRoom(ChatRoom.builder().id(roomId).build())
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response {
        private String message;
        private MemberResponseDto sender;

        public static Response of(ChatMessage message) {
            return Response.builder()
                    .message(message.getMessage())
                    .sender(MemberResponseDto.of(message.getSender()))
                    .build();
        }
    }

}
