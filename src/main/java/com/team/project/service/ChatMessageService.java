package com.team.project.service;

import com.team.project.domain.ChatMessage;
import com.team.project.dto.request.ChatMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChannelTopic channelTopic;
    private final RedisTemplate redisTemplate;

    // destination정보에서 roomId 추출
    public String getRoomId(String destination) {
        int lastIndex = destination.lastIndexOf('/');
        if (lastIndex != -1)
            return destination.substring(lastIndex + 1);
        else
            throw new IllegalArgumentException("lastIndex 오류입니다.");
    }

    // 채팅방에 메시지 발송
    public void sendChatMessage(ChatMessage chatMessage) {

        if (ChatMessage.MessageType.ENTER.equals(chatMessage.getType())) {
            chatMessage.setMessage(chatMessage.getSender() + "님이 방에 입장했습니다.");
        } else if (ChatMessage.MessageType.QUIT.equals(chatMessage.getType())) {
            chatMessage.setMessage(chatMessage.getSender() + "님이 방에서 나갔습니다.");
        }
        log.info("sender, sendMessage: {}, {}", chatMessage.getSender(), chatMessage.getMessage());
        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessage);
    }


    private Page<ChatMessageDto> chatResponseDto(Page<ChatMessage> postSlice) {
        return postSlice.map(p ->
                ChatMessageDto.builder()
                        .type(p.getType())
                        .roomId(p.getRoomId())
                        .sender(p.getSender())
                        .message(p.getMessage())
                        .build());
    }
}
