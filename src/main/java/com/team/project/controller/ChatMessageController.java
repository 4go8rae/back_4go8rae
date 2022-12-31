package com.team.project.controller;

import com.team.project.domain.ChatMessage;
import com.team.project.dto.request.ChatMessageDto;
import com.team.project.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatService chatService;

    @MessageMapping("/api/chat/message")
    public void enter(ChatMessageDto message) {
        chatService.save(message);
    }

    @GetMapping("/chat/message/{roomId}")
    public List<ChatMessage> getMessage(@PathVariable String roomId) {
        return chatService.getMessages(roomId);
    }

}

