package com.team.project.repository;

import com.team.project.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    ChatRoom findByRoomId(String roomId);
    Optional<ChatRoom> findByName(String name);
//    List<ChatRoom> findAllByOrderByCreatedAtDesc();
}
