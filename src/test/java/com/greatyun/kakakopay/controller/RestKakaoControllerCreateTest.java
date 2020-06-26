package com.greatyun.kakakopay.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greatyun.kakakopay.controller.dto.CreateMoneyDTO;
import com.greatyun.kakakopay.domain.ChatRoom;
import com.greatyun.kakakopay.domain.Member;
import com.greatyun.kakakopay.domain.MemberRoomMap;
import com.greatyun.kakakopay.domain.MoneyResult;
import com.greatyun.kakakopay.repository.ChatRoomRepository;
import com.greatyun.kakakopay.repository.MemberRepository;
import com.greatyun.kakakopay.repository.MemberRoomMapRepository;
import com.greatyun.kakakopay.repository.MoneyResultRepository;
import com.greatyun.kakakopay.service.KakaoPayService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 뿌리기 등록 API 테스트
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class RestKakaoControllerCreateTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private MemberRoomMapRepository memberRoomMapRepository;

    @Autowired
    private MoneyResultRepository moneyResultRepository;

    @Value("${kakaopay.room.id}")
    private String headerRoomId;

    @Value("${kakaopay.user.id}")
    private String headerUserId;


    private Member member;
    private ChatRoom chatRoom;
    private MemberRoomMap memberRoomMap;

    @BeforeEach
    public void before() {
        this.member = generateMember();
        this.chatRoom = generateChatRoom(member);
        this.memberRoomMap = generateMemberChatRoomMapping(member, chatRoom);
    }

    @DisplayName("뿌리기 등록 테스트")
    @Test
    public void createMoneyTest() throws Exception {
        // given
        CreateMoneyDTO createMoneyDTO = CreateMoneyDTO.builder()
                .money(10000)
                .peopleCnt(10)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.set(headerUserId , String.valueOf(member.getPkid()));
        headers.set(headerRoomId , String.valueOf(chatRoom.getPkid()));

        mockMvc.perform(post("/api/v1.0/money")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createMoneyDTO))
                .headers(headers)
            ).andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.token").isString())
                ;

    }

    @Test
    @DisplayName("뿌리기 등록 후 인원수 및 금액 확인")
    public void createMoneyPeopleCntTest() throws Exception {
        // given
        int peopleCnt = 10;
        int money = 10000;
        CreateMoneyDTO createMoneyDTO = CreateMoneyDTO.builder()
                .money(money)
                .peopleCnt(peopleCnt)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.set(headerUserId , String.valueOf(member.getPkid()));
        headers.set(headerRoomId , String.valueOf(chatRoom.getPkid()));

        mockMvc.perform(post("/api/v1.0/money")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createMoneyDTO))
                .headers(headers)
        ).andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.token").exists())
        ;

        List<MoneyResult> moneyResults = moneyResultRepository.findAll();

        // 인원수 분배 확인
        assertThat(moneyResults.size()).isEqualTo(peopleCnt);
        // 분배 가격 동일한지 확인
        assertThat(moneyResults.stream().mapToInt(x -> x.getRecvMoney()).sum()).isEqualTo(money);

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