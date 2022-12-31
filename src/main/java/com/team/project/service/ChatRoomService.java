package com.team.project.service;

import com.team.project.domain.ChatRoom;
import com.team.project.domain.Member;
import com.team.project.repository.ChatRoomRepository;
import com.team.project.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
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



    // redis 에 입장정보로 sessionId 와 roomId를 저장하고 해단 sessionId 와 토큰에서 받아온 userId를 저장함
    public void setUserEnterInfo(String sessionId, Long memberId, String roomId) {
        hashOpsEnterInfo.put(ENTER_INFO, sessionId, roomId);
        hashOpsUserInfo.put(USER_INFO, sessionId, Long.toString(memberId));
    }

    // redis 에 저장했던 sessionId 로 roomId를 리턴함
    public String getUserEnterRoomId(String memberId) {
        return hashOpsEnterInfo.get(ENTER_INFO, memberId);
    }

    // redis 에 저장했던 sessionId 로 userId 를 얻어오고 해당 userId 로 Member 객체를 찾아 리턴함
    public Member checkSessionUser(String sessionId) {
        Long memberId = Long.parseLong(Objects.requireNonNull(hashOpsUserInfo.get(USER_INFO, sessionId)));
        return memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));
    }

    // 유저가 나갈때 redis 에 저장했던 해당 세션 / 유저의 정보를 삭제함
    public void removeUserEnterInfo(String sessionId) {
        hashOpsEnterInfo.delete(ENTER_INFO, sessionId);
        hashOpsUserInfo.delete(USER_INFO, sessionId);
    }

}
