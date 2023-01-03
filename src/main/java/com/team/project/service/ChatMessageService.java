package com.team.project.service;

import com.team.project.domain.ChatMessage;
import com.team.project.domain.Member;
import com.team.project.dto.request.ChatMessageDto;
import com.team.project.exception.CustomException;
import com.team.project.exception.ErrorCode;
import com.team.project.jwt.JwtTokenProvider;
import com.team.project.jwt.UserDetailsImpl;
import com.team.project.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChannelTopic channelTopic;
    private final RedisTemplate redisTemplate;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider tokenProvider;


    // 채팅방에 메시지 발송
    public void sendChatMessage(ChatMessageDto messageRequestDto) {

        ChatMessage chatMessage = ChatMessage.builder()
                .type(messageRequestDto.getType())
                .roomId(messageRequestDto.getRoomId())
                .message(messageRequestDto.getMessage())
                .sender(messageRequestDto.getSender())
                .build();

        if (ChatMessage.MessageType.ENTER.equals(chatMessage.getType())) {
            chatMessage.setMessage(chatMessage.getSender() + "님이 방에 입장했습니다.");

        } else if (ChatMessage.MessageType.QUIT.equals(chatMessage.getType())) {
            chatMessage.setMessage(chatMessage.getSender() + "님이 방에서 나갔습니다.");
        }

        log.info("sender, sendMessage: {}, {}", chatMessage.getSender(), chatMessage.getMessage());
        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessage);
    }


    // destination정보에서 roomId 추출
    public String getRoomId(String destination) {
        int lastIndex = destination.lastIndexOf('/');
        if (lastIndex != -1)
            return destination.substring(lastIndex + 1);
        else
            throw new IllegalArgumentException("lastIndex 오류입니다.");
    }
}
