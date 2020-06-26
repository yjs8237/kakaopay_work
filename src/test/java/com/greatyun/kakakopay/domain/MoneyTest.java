package com.greatyun.kakakopay.domain;

import com.greatyun.kakakopay.repository.MemberRepository;
import com.greatyun.kakakopay.repository.MoneyRepository;
import com.greatyun.kakakopay.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MoneyTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MoneyRepository moneyRepository;

    @Autowired
    private RoomRepository roomRepository;

    private ChatRoom chatRoom;

    private Member member;

    @BeforeEach
    public void before() {
        String name = "greatyun";
        String email = "email@email.com";


        Member member = Member.builder()
                .name(name)
                .email(email)
                .build();

        this.member = memberRepository.save(member);

        ChatRoom chatRoom = ChatRoom.builder()
                .roomName("room")
                .ownerMemberId(this.member.getPkid())
                .build();

        this.chatRoom = roomRepository.save(chatRoom);
    }

    @Test
    @DisplayName("뿌리기 등록 테스트")
    public void createMoneyTest() throws Exception{

        // 뿌리기 금액
        int moneyValue = 10000;
        Money money = Money.builder()
                .money(moneyValue)
                .build();
        money.createMoney(this.member , this.chatRoom.getPkid());
        // 뿌리기 생성
        Money savedMoney = moneyRepository.save(money);

        assertThat(savedMoney.getMember().getPkid()).isEqualTo(this.member.getPkid());
        assertThat(savedMoney.getMoney()).isEqualTo(moneyValue);
        assertThat(savedMoney.getRoomId()).isEqualTo(this.chatRoom.getPkid());
    }

}