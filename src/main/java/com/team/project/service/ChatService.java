package com.team.project.service;

import com.team.project.domain.ChatMessage;
import com.team.project.dto.request.ChatMessageDto;
import com.team.project.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChannelTopic channelTopic;
    private static final String CHAT_MESSAGE = "CHAT_MESSAGE";
    private static final String ENTER_INFO = "ENTER_INFO";

    private RedisTemplate<String, Object> redisTemplate;
    private StringRedisTemplate stringRedisTemplate;

    private HashOperations<String, String, List<ChatMessageDto>> opsHashChatMessage;
    private HashOperations<String, String, String> hashOpsEnterInfo;
    private ValueOperations<String, String> valueOps;



    @Transactional
    public ChatMessageDto save(ChatMessageDto chatMessageDto) {
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(ChatMessage.class));
        String roomId = chatMessageDto.getRoomId();
        List<ChatMessageDto> chatMessageList = opsHashChatMessage.get(CHAT_MESSAGE, roomId);

        if (chatMessageList == null) {
            chatMessageList = new ArrayList<>();
        }
        chatMessageList.add(chatMessageDto);

        opsHashChatMessage.put(CHAT_MESSAGE, roomId, chatMessageList);
        redisTemplate.expire(CHAT_MESSAGE, 24, TimeUnit.HOURS);
        return chatMessageDto;
    }

    @Transactional
    public List<ChatMessageDto> findAllMessage(String roomId) {
        List<ChatMessageDto> chatMessageDtoList = new ArrayList<>();

        if (opsHashChatMessage.size(CHAT_MESSAGE) > 0) {
            return (opsHashChatMessage.get(CHAT_MESSAGE, roomId));
        }
        else {
            List<ChatMessage> chatMessages = chatMessageRepository.findAllByRoomId(roomId);

            for (ChatMessage chatMessage : chatMessages) {
                ChatMessageDto chatMessageDto = new ChatMessageDto(chatMessage);
                chatMessageDtoList.add(chatMessageDto);
            }
            opsHashChatMessage.put(CHAT_MESSAGE, roomId, chatMessageDtoList);
            return chatMessageDtoList;
        }
    }

    /* destination에서 roomId 가져오기 */
    public String getRoomId(String destination) {
        int lastIndex = destination.lastIndexOf('/');
        if (lastIndex != -1) return destination.substring(lastIndex + 1);
        else return "";
    }

    public void sendChatMessage(ChatMessage chatMessage) {
        if (ChatMessage.MessageType.ENTER.equals(chatMessage.getType())) {
            chatMessage.setMessage(chatMessage.getSender() + "님이 방에 입장하였습니다.");
        } else if (ChatMessage.MessageType.QUIT.equals(chatMessage.getType())) {
            chatMessage.setMessage(chatMessage.getSender() + "님이 방에서 나갔습니다.");
        }
        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessage);
    }

    public void setUserEnterInfo(String roomId, String sessionId) {
        hashOpsEnterInfo.put(ENTER_INFO, roomId, sessionId);
    }

    public void removeUserEnterInfo(String roomId, String sessionId) {
        hashOpsEnterInfo.delete(ENTER_INFO, roomId, sessionId);
    }

}
