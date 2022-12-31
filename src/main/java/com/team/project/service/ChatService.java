package com.team.project.service;

import com.team.project.domain.ChatMessage;
import com.team.project.domain.ChatRoom;
import com.team.project.domain.Member;
import com.team.project.dto.request.ChatMessageDto;
import com.team.project.dto.request.ChatRoomDto;
import com.team.project.repository.ChatMessageRepository;
import com.team.project.repository.ChatRoomRepository;
import com.team.project.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.*;


@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

    private final RedisPublisher redisPublisher;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomService chatRoomService;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatMessageService chatMessageService;
    private Map<String, ChatRoomDto> chatRooms;
    private final MemberRepository memberRepository;

    @Transactional
    public void save(ChatMessageDto message) {
        if (ChatMessage.MessageType.ENTER.equals(message.getType())) {
            chatRoomService.enterChatRoom(message.getRoomId());
        }

        Optional<Member> member = memberRepository.findByNickname(message.getSender());
        String nickname = member.get().getNickname();

        ChatMessage chatMessage = ChatMessage.builder()
                .type(message.getType())
                .roomId(message.getRoomId())
                .sender(nickname)
                .message(message.getMessage())
                .build();
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(chatMessage.getRoomId());

        chatMessageRepository.save(chatMessage);
        ChatMessage chatMessageRedis = chatMessageService.save(chatMessage);

        redisPublisher.publish(ChatRoomService.getTopic(chatMessage.getRoomId()), chatMessage);
    }

    @PostConstruct
    private void init() {
        chatRooms = new LinkedHashMap<>();
    }

    public List<ChatRoomDto> findAllRoom() {
        List<ChatRoomDto> result = new ArrayList<>(chatRooms.values());
        Collections.reverse(result);
        return result;
    }

    public ChatRoomDto findByRoomId(String roomId) {
        return chatRooms.get(roomId);
    }

    public List<ChatMessage> getMessages(String roomId) {
        return chatMessageService.findAllMessage(roomId);
    }

    @Transactional
    public List<ChatRoomDto> findAllRoomAll() {
        List<ChatRoom> chatRoomList = chatRoomRepository.findAll();
        List<ChatRoomDto> chatRoomDtos = new ArrayList<>();

        for (ChatRoom chatRoom : chatRoomList) {
            chatRoomDtos.add(
                    ChatRoomDto.builder()
                            .roomId(chatRoom.getRoomId())
                            .name(chatRoom.getName())
                            .build());
        }

        return chatRoomDtos;
    }
}
