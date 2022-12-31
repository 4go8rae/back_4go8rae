package com.team.project.service;

import com.team.project.domain.ChatMessage;
import com.team.project.dto.request.ChatMessageDto;
import com.team.project.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private static final String CHAT_MESSAGE = "CHAT_MESSAGE";
    public static final String ENTER_INFO = "ENTER_INFO";
    private final ChatMessageRepository chatMessageRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private HashOperations<String, String, String> hashOpsEnterInfo;
    private HashOperations<String, String, List<ChatMessageDto>> opsHashChatMessageDto;
    private HashOperations<String, String, List<ChatMessage>> opsHashChatMessage;
    private ValueOperations<String, String> valueOps;

    @PostConstruct
    private void init() {
        opsHashChatMessage = redisTemplate.opsForHash();
        hashOpsEnterInfo = redisTemplate.opsForHash();
        valueOps = stringRedisTemplate.opsForValue();
    }

    @Transactional
    public ChatMessage save(ChatMessage chatMessage) {
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(ChatMessage.class));
        String roomId = chatMessage.getRoomId();
        List<ChatMessage> chatMessageList = opsHashChatMessage.get(CHAT_MESSAGE, roomId);

        if (chatMessageList == null) {
            chatMessageList = new ArrayList<>();
        }
        chatMessageList.add(chatMessage);

        opsHashChatMessage.put(CHAT_MESSAGE, roomId, chatMessageList);
        redisTemplate.expire(CHAT_MESSAGE,24, TimeUnit.HOURS);

        return chatMessage;
    }


    @Transactional
    public List<ChatMessage> findAllMessage(String roomId) {
        List<ChatMessage> chatMessageList = new ArrayList<>();

        if (opsHashChatMessage.size(CHAT_MESSAGE) > 0) {

            return opsHashChatMessage.get(CHAT_MESSAGE, roomId);

        } else {
            List<ChatMessage> chatMessages = chatMessageRepository.findAllByRoomId(roomId);

            chatMessageList.addAll(chatMessages);
            opsHashChatMessage.put(CHAT_MESSAGE, roomId, chatMessageList);

            return chatMessageList;
        }
    }
}
