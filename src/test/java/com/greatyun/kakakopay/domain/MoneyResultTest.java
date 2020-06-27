package com.greatyun.kakakopay.domain;

import com.greatyun.kakakopay.common.CommonUtil;
import com.greatyun.kakakopay.enumuration.EnumFinishYn;
import com.greatyun.kakakopay.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@DataJpaTest
class MoneyResultTest {

    @MockBean
    private CommonUtil commonUtil;

    @Autowired
    private MoneyResultRepository moneyResultRepository;

    @Autowired
    private MoneyRepository moneyRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private MemberRoomMapRepository memberRoomMapRepository;

    @DisplayName("뿌리기 등록한 인원수와 해당 엔티티의 등록 수와 같아야 한다")
    @Test
    public void checkPeopleCnt() throws Exception {
        String token = "123";
        int peopleCnt = 10;
        int moneyValue = 10000;
        Member member = generateMember();
        ChatRoom chatRoom = generateChatRoom(member);

        Money money = Money.builder()
                .token(token)
                .peopleCnt(peopleCnt)
                .money(moneyValue)
                .finishYn(EnumFinishYn.N)
                .build();
        money.createMoney(member , chatRoom.getPkid());
        Money savedMoney = moneyRepository.save(money);

        List<Integer> divideList = new ArrayList<>();
        for (int i = 0; i < peopleCnt; i++) {
            divideList.add(moneyValue / peopleCnt);
        }
        given(commonUtil.divideMoneyRandomly(peopleCnt , moneyValue)).willReturn(divideList);

        for (Integer divideValue : divideList) {
            MoneyResult moneyResult = MoneyResult.builder()
                    .recvMoney(divideValue)
                    .finishYn(EnumFinishYn.N)
                    .money(money)
                    .build();
            moneyResultRepository.save(moneyResult);
        }

        List<MoneyResult> resultList = moneyResultRepository.findAll();

        assertThat(resultList.size()).isEqualTo(peopleCnt);
        assertThat(resultList.stream().mapToInt(x -> x.getRecvMoney()).sum()).isEqualTo(moneyValue);

    }

    private Member generateMember() {
        Member member = Member.builder()
                .email("email")
                .name("greatyun")
                .build();
        return memberRepository.save(member);
    }

    private ChatRoom generateChatRoom(Member member) {
        ChatRoom chatRoom = ChatRoom.builder()
                .ownerMemberId(member.getPkid())
                .roomName("room-1")
                .build();
        return chatRoomRepository.save(chatRoom);
    }

    private MemberRoomMap generateMemberChatRoomMapping(Member member , ChatRoom chatRoom) {
        MemberRoomMap memberRoomMap = MemberRoomMap.builder()
                .build();
        memberRoomMap.joinRoomAndMemberMap(member , chatRoom);
        return memberRoomMapRepository.save(memberRoomMap);
    }

}