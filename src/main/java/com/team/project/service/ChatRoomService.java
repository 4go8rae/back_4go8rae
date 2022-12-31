package com.team.project.service;

import com.team.project.domain.ChatRoom;
import com.team.project.dto.request.ChatRoomDto;
import com.team.project.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final RedisMessageListenerContainer redisMessageListener;
    private final RedisSubscriber redisSubscriber;
    private static final String CHAT_ROOMS = "CHAT_ROOM";
    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, ChatRoomDto> opsHashChatRoomDto;
    private HashOperations<String, String, ChatRoom> opsHashChatRoom;
    private final StringRedisTemplate stringRedisTemplate; // StringRedisTemplate 사용
    private static ValueOperations<String, String> topics;
    private final ChatRoomRepository chatRoomRepository;

    @PostConstruct
    private void init() {
        opsHashChatRoom = redisTemplate.opsForHash();
        topics = stringRedisTemplate.opsForValue();
    }

    public List<ChatRoom> findAllRoom() {
        List<ChatRoom> result = new ArrayList<>(opsHashChatRoom.values(CHAT_ROOMS));
        Collections.reverse(result);
        return result;
    }

    public ChatRoom findRoomByRoomId(String roomId) {
        return opsHashChatRoom.get(CHAT_ROOMS, roomId);
    }

    public ChatRoom checkRoom(String name) {
        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findByName(name);
        return optionalChatRoom.orElse(null);
    }

    @Transactional
    public ChatRoom createRoom(String roomName) {
        ChatRoom existChatRoom = checkRoom(roomName);

        if (existChatRoom == null) {
            ChatRoom chatRoom = ChatRoom.create(roomName);

            // redis 저장
            opsHashChatRoom.put(CHAT_ROOMS, chatRoom.getRoomId(), chatRoom);
            redisTemplate.expire(CHAT_ROOMS, 48, TimeUnit.HOURS);

            // db 저장
            chatRoomRepository.save(chatRoom);

            return chatRoom;
        }

        String existChatRoomId = existChatRoom.getRoomId();
        String existChatRoomName = existChatRoom.getName();

        return ChatRoom.builder()
                .roomId(existChatRoomId)
                .name(existChatRoomName)
                .build();
    }

    public void enterChatRoom(String roomId) {
        if (topics.get(roomId) == null) {
            ChannelTopic topic = new ChannelTopic(roomId);
            redisMessageListener.addMessageListener(redisSubscriber, topic);
            topics.set(roomId, topic.toString());
            redisTemplate.expire(roomId, 48, TimeUnit.HOURS);
        } else {
            String topicToString = topics.get(roomId);
            ChannelTopic topic = new ChannelTopic(topicToString);
            redisMessageListener.addMessageListener(redisSubscriber, topic);
        }
    }

    public static ChannelTopic getTopic(String roomId) {
        return new ChannelTopic(roomId);
    }

}
