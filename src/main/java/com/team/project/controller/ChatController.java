package com.team.project.controller;

import com.team.project.domain.ChatMessage;
import com.team.project.dto.request.ChatMessageDto;
import com.team.project.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;



@RequiredArgsConstructor
@Controller
public class ChatController {

    private final ChatService chatService;


    /**
     * websocket "/pub/api/chat/message"로 들어오는 메시징을 처리한다.
     */
    @MessageMapping("/api/chat/message")
    public void message(@RequestBody ChatMessageDto message) {
//        Member member = userDetails.getMember();
        ChatMessage chatMessage = ChatMessage.of(message);
//        chatMessage.setSender(member);
        chatService.sendChatMessage(chatMessage);
    }

}