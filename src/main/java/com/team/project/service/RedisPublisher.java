package com.team.project.service;

import com.team.project.domain.ChatMessage;
import com.team.project.dto.request.ChatMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RedisPublisher {
    private final RedisTemplate<String, Object> redisTemplate;

    /* websocket에서 받아온 메세지를 convertAndSend를 통하여 Redis의 messageListener로 발행 */
    public void publish(ChannelTopic topic, ChatMessageDto message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }
}
