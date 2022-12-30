package com.team.project.controller;

import com.team.project.domain.ChatMessage;
import com.team.project.dto.request.ChatMessageDto;
import com.team.project.service.ChatService;
import com.team.project.service.RedisPublisher;
import com.team.project.service.RedisSubscriber;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import static com.team.project.domain.ChatMessage.MessageType.ENTER;


@RequiredArgsConstructor
@Controller
public class ChatController {
    private final RedisPublisher redisPublisher;
    private final RedisSubscriber redisSubscriber;
    private final ChatService chatService;
    private final RedisMessageListenerContainer redisMessageListener;


    /**
     * websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
     */
    @MessageMapping("/api/chat/message")
    public void message(ChatMessageDto message) {

        ChannelTopic topic = new ChannelTopic("/sub/chat/room" + message.getRoomId());

        redisPublisher.publish(topic, message);
//        redisPublisher.publish(chatService.getTopic(message.getRoomId()), message);
    }

    @MessageMapping("/enter")
    public void enterRoom(ChatMessage message) {
        message.setType(ENTER);
        ChannelTopic topic = new ChannelTopic("/sub/chat/room/" + message.getRoomId());
        redisMessageListener.addMessageListener(redisSubscriber, topic);
    }
}