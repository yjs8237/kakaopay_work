package com.greatyun.kakakopay.repository;

import com.greatyun.kakakopay.domain.MemberRoomMap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRoomMapRepository extends JpaRepository<MemberRoomMap, Long> {

    Optional<MemberRoomMap> findByMember_PkidAndChatRoom_Pkid(Long memberId , Long chatRoomId);

}
