package com.greatyun.kakakopay.repository;

import com.greatyun.kakakopay.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
}
