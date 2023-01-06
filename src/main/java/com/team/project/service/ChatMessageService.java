package com.team.project.service;

import com.team.project.dto.request.ChatMessageDto;
import com.team.project.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    // 채팅방에 메시지 발송
    @Transactional
    public void sendMessage(ChatMessageDto.Send message) {
        chatMessageRepository.save(message.toMessage());
    }

}
