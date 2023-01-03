package com.team.project.controller;

import com.team.project.domain.ChatRoom;
import com.team.project.jwt.UserDetailsImpl;
import com.team.project.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/chat")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    // 모든 채팅방 목록 조회
    @GetMapping("/rooms")
    public List<ChatRoom> getAllRooms() {
        return chatRoomService.findAllRoom();
    }

    // 채팅방 생성
    @PostMapping("/room")
    public ChatRoom createRoom(@RequestParam String name,
                               @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return chatRoomService.createChatRoom(name, userDetails);
    }

    // 특정 채팅방 조회
    @GetMapping("/room/{roomId}")
    public Optional<ChatRoom> roomInfo(@PathVariable String roomId) {
        return chatRoomService.findRoomByRoomId(roomId);
    }


}