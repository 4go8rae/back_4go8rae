package com.team.project.controller;

import com.team.project.domain.ChatMessage;
import com.team.project.domain.ChatRoom;
import com.team.project.dto.request.ChatMessageDto;
import com.team.project.service.ChatService;
import com.team.project.service.RedisPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;


@RequiredArgsConstructor
@Controller
public class ChatController {
    private final RedisPublisher redisPublisher;
    private final ChatService chatService;


    /**
     * websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
     */
    @MessageMapping("/api/chat/message")
    public void message(ChatMessageDto message) {

        ChatRoom chatRoom = chatService.findRoomByRoomId(message.getRoomId());
        ChatMessage chatMessage =  ChatMessage.createChatMessage(chatRoom, message.getType(), message.getSender(), message.getRoomId(), message.getMessage());

        if (ChatMessage.MessageType.ENTER.equals(message.getType())) {
            chatMessage.setSender("[알림]");
            chatMessage.setMessage(message.getSender() + "님이 입장하셨습니다.");
        }
        else if (ChatMessage.MessageType.QUIT.equals(message.getType())) {
            chatMessage.setSender("[알림]");
            chatMessage.setMessage(message.getSender() + "님이 퇴장하셨습니다.");
        }

        chatRoom.addChatMessage(chatMessage);

//        sendingOperations.convertAndSend("/sub/" + message.getRoomId(), message.getMessage());
        // Websocket에 발행된 메시지를 redis로 발행한다(publish)

        redisPublisher.publish(chatService.getTopic(message.getRoomId()), message);
    }

}