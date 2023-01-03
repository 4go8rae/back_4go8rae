package com.team.project.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.project.domain.ChatMessage;
import com.team.project.dto.response.ChatMessageResponseDto;
import com.team.project.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisSubscriber {

    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;


    // 클라이언트에서 메세지가 도착하면 해당 메세지를 messagingTemplate 으로 컨버팅하고 다른 구독자들에게 전송한뒤 해당 메세지를 DB에 저장함
    public void sendMessage(String publishMessage) {

        try {
            log.info("publishM = {}", publishMessage);
            ChatMessage chatMessage = objectMapper.readValue(publishMessage, ChatMessage.class);

            messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getRoomId(), getPayload(chatMessage));

            ChatMessage message = ChatMessage.builder()
                    .type(chatMessage.getType())
                    .roomId(chatMessage.getRoomId())
                    .message(chatMessage.getMessage())
                    .sender(chatMessage.getSender())
                    .build();
            chatMessageRepository.save(message);

        }
        catch (Exception e) {
            log.error("Exception {}", e);
        }

    }

    private ChatMessageResponseDto getPayload(ChatMessage chatMessage) {
        return ChatMessageResponseDto.builder()
                .type(chatMessage.getType())
                .roomId(chatMessage.getRoomId())
                .message(chatMessage.getMessage())
                .sender(chatMessage.getSender())
                .build();
    }
}
