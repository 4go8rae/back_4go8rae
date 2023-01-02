package com.team.project.service;

import com.team.project.domain.ChatRoom;
import com.team.project.domain.Member;
import com.team.project.repository.ChatRoomRepository;
import com.team.project.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    // Redis CacheKeys
    private static final String CHAT_ROOMS = "CHAT_ROOM";
    public static final String USER_COUNT = "USER_COUNT";
    public static final String ENTER_INFO = "ENTER_INFO";
    public static final String USER_INFO = "USER_INFO";

    private HashOperations<String, String, String> hashOpsEnterInfo;
    private HashOperations<String, String, String> hashOpsUserInfo;
    private HashOperations<String, String, ChatRoom> opsHashChatRoom;
    private final RedisTemplate<String, Object> redisTemplate;
    private Map<String, ChannelTopic> topics;
    private ValueOperations<String, String> valueOps;

    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;


    @PostConstruct
    private void init() {
        opsHashChatRoom = redisTemplate.opsForHash();
        hashOpsEnterInfo = redisTemplate.opsForHash();

        topics = new HashMap<>();
    }

    public List<ChatRoom> findAllRoom() {
        return chatRoomRepository.findAll();
    }

    public Optional<ChatRoom> findRoomByRoomId(String roomId) {
        return chatRoomRepository.findByRoomId(roomId);
    }

    public ChatRoom createChatRoom(String name) {
        ChatRoom chatRoom = ChatRoom.create(name);
        opsHashChatRoom.put(CHAT_ROOMS, chatRoom.getRoomId(), chatRoom);
        chatRoomRepository.save(chatRoom);
        return chatRoom;
    }

}
