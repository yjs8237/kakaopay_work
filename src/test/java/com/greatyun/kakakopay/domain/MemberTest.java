package com.greatyun.kakakopay.domain;

import com.greatyun.kakakopay.exception.KakaoPayException;
import com.greatyun.kakakopay.repository.MemberRepository;
import com.greatyun.kakakopay.repository.MemberRoomMapRepository;
import com.greatyun.kakakopay.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MemberTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private MemberRoomMapRepository memberRoomMapRepository;

    private String email;

    private String name;

    @BeforeEach
    public void before() {
        this.name = "greatyun";
        this.email = "greatyun@email.com";
    }

    @Test
    @DisplayName("회원 등록 테스트")
    public void registerMemberTest() throws KakaoPayException {

        Member member = Member.builder()
                .email(email)
                .name(name)
                .build();
        Member savedMember = memberRepository.save(member);

        assertThat(savedMember).isNotNull();
        assertThat(savedMember.getEmail()).isEqualTo(email);
        assertThat(savedMember.getName()).isEqualTo(name);

    }

    @Test
    @DisplayName("회원과 대화방의 매핑 연관관계 테스트")
    public void memberRoomMapTest() throws KakaoPayException {
        // given
        /**
         * 1. 회원 생성
         */
        Member member = Member.builder()
                .email(email)
                .name(name)
                .build();
        Member savedMember = memberRepository.save(member);
        // 2. 대화방 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .ownerMemberId(savedMember.getPkid())
                .build();
        ChatRoom savedChatRoom = roomRepository.save(chatRoom);


        // when
        // 3. 대화방 참여
        MemberRoomMap memberRoomMap = MemberRoomMap.builder()
                .build();
        memberRoomMap.joinRoomAndMemberMap(savedMember , savedChatRoom);
        MemberRoomMap savedMap = memberRoomMapRepository.save(memberRoomMap);


        // then
        // 회원이 참여한 대화방 확인
        assertThat(savedMember.getMyRoomMapList().size()).isEqualTo(1);
        // 대화방 방장 확인
        assertThat(savedMember.getPkid()).isEqualTo(savedChatRoom.getOwnerMemberId());
        // 회원 매핑 확인
        assertThat(savedMap.getMember().getPkid()).isEqualTo(savedMember.getPkid());
        // 대화방 매핑 확인
        assertThat(savedMap.getChatRoom().getPkid()).isEqualTo(savedChatRoom.getPkid());

    }


}