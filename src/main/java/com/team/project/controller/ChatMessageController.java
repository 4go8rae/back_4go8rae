package com.team.project.controller;

import com.team.project.domain.ChatMessage;
import com.team.project.dto.request.ChatMessageDto;
import com.team.project.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    // /pub/chat/message 로 들어오는 메시징 처리
    @MessageMapping("/chat/message")
    public void message(@RequestBody ChatMessageDto messageRequestDto) {
        ChatMessage chatMessage = new ChatMessage(messageRequestDto);
        chatMessageService.sendChatMessage(chatMessage);
    }

}