package com.team.project.controller;

import com.team.project.domain.ChatRoom;
import com.team.project.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ChatRoom createRoom(@RequestParam String name) {
        return chatRoomService.createChatRoom(name);
    }

    // 특정 채팅방 조회
    @GetMapping("/room/{roomId}")
    public ChatRoom roomInfo(@PathVariable String roomId) {
        return chatRoomService.findRoomByRoomId(roomId);
    }


}