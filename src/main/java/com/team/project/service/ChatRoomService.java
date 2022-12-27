package com.team.project.service;

import org.springframework.stereotype.Service;

@Service
public class ChatRoomService {

    /* destination에서 roomId 가져오기 */
    public String getRoomId(String destination) {
        int lastIndex = destination.lastIndexOf('/');
        if (lastIndex != -1) return destination.substring(lastIndex + 1);
        else return "";
    }
}
