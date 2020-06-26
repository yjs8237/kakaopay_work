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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ChatRoomTest {

    private Member member;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private MemberRoomMapRepository memberRoomMapRepository;

    @BeforeEach
    public void before() {
        Member member = Member.builder()
                .name("greatyun")
                .email("greatyun@email.com")
                .build();
        Member savedMember = memberRepository.save(member);
        this.member = savedMember;
    }


    @Test
    @DisplayName("대화방 생성 테스트")
    public void createRoomTest() throws KakaoPayException {

        // 방생성
        String roomName = "대화방";
        ChatRoom chatRoom = ChatRoom.builder()
                .ownerMemberId(this.member.getPkid())
                .roomName(roomName)
                .build();
        ChatRoom savedChatRoom = roomRepository.save(chatRoom);

        // 대화방 방장 회원 매핑
        MemberRoomMap memberRoomMap = MemberRoomMap.builder()
                .build();
        memberRoomMap.joinRoomAndMemberMap(this.member , savedChatRoom);


        assertThat(savedChatRoom.getOwnerMemberId()).isEqualTo(this.member.getPkid());
        assertThat(savedChatRoom.getRoomName()).isEqualTo(roomName);
        assertThat(memberRoomMap.getChatRoom().getPkid()).isEqualTo(savedChatRoom.getPkid());
        assertThat(memberRoomMap.getMember().getPkid()).isEqualTo(this.member.getPkid());

    }

    @Test
    @DisplayName("유저 대화방 참여 테스트")
    public void joinRoomTest() throws KakaoPayException {

        // 방생성
        String roomName = "대화방";
        ChatRoom chatRoom = ChatRoom.builder()
                .ownerMemberId(this.member.getPkid())
                .roomName(roomName)
                .build();
        ChatRoom savedChatRoom = roomRepository.save(chatRoom);

        // 회원생성
        for (int i = 0; i < 3; i++) {
            Member member = Member.builder()
                    .email("new@email.com" + i)
                    .name("new" + i)
                    .build();
            Member savedMember = memberRepository.save(member);

            MemberRoomMap memberRoomMap = MemberRoomMap.builder()
                    .build();
            memberRoomMap.joinRoomAndMemberMap(savedMember , savedChatRoom);
        }

        // when
        List<MemberRoomMap> roomMaps = memberRoomMapRepository.findAll();

        // then
        assertThat(roomMaps.size()).isEqualTo(3);
        for (int i = 0; i < 3; i++) {
            assertThat(roomMaps.get(i).getChatRoom().getPkid()).isEqualTo(savedChatRoom.getPkid());
        }

    }

}