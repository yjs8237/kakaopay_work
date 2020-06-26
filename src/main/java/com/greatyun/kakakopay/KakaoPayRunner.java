package com.greatyun.kakakopay;

import com.greatyun.kakakopay.domain.ChatRoom;
import com.greatyun.kakakopay.domain.Member;
import com.greatyun.kakakopay.domain.MemberRoomMap;
import com.greatyun.kakakopay.repository.ChatRoomRepository;
import com.greatyun.kakakopay.repository.MemberRepository;
import com.greatyun.kakakopay.repository.MemberRoomMapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class KakaoPayRunner implements CommandLineRunner {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private MemberRoomMapRepository memberRoomMapRepository;


    @Override
    public void run(String... args) throws Exception {

        /**
         * 테스트 하기 위한 사전 데이터 세팅
         * 유저 , 대화방
         */

        ChatRoom savedRoom = null;
        for (int i = 0; i < 5; i++) {
            Member member = Member.builder()
                    .name("jisang" + i)
                    .email("email@email.com" + i)
                    .build();
            Member savedMember = memberRepository.save(member);

            if(i == 0) {
                ChatRoom chatRoom = ChatRoom.builder()
                        .roomName("room-1")
                        .ownerMemberId(savedMember.getPkid())
                        .build();
                savedRoom = chatRoomRepository.save(chatRoom);
            }

            MemberRoomMap memberRoomMap = MemberRoomMap.builder()
                    .build();
            memberRoomMap.joinRoomAndMemberMap(savedMember , savedRoom);
            memberRoomMapRepository.save(memberRoomMap);
        }

        savedRoom = null;
        for (int i = 0; i < 5; i++) {
            Member member = Member.builder()
                    .name("another" + i)
                    .email("email@email.com" + i)
                    .build();
            Member savedMember = memberRepository.save(member);

            if(i == 0) {
                ChatRoom chatRoom = ChatRoom.builder()
                        .roomName("room-2")
                        .ownerMemberId(savedMember.getPkid())
                        .build();
                savedRoom = chatRoomRepository.save(chatRoom);
            }

            MemberRoomMap memberRoomMap = MemberRoomMap.builder()
                    .build();
            memberRoomMap.joinRoomAndMemberMap(savedMember , savedRoom);
            memberRoomMapRepository.save(memberRoomMap);
        }

    }
}
