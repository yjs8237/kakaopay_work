package com.greatyun.kakakopay.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greatyun.kakakopay.common.CommonUtil;
import com.greatyun.kakakopay.controller.dto.CreateMoneyDTO;
import com.greatyun.kakakopay.controller.dto.RecvMoneyDTO;
import com.greatyun.kakakopay.domain.ChatRoom;
import com.greatyun.kakakopay.domain.Member;
import com.greatyun.kakakopay.domain.MemberRoomMap;
import com.greatyun.kakakopay.domain.Money;
import com.greatyun.kakakopay.enumuration.EnumFinishYn;
import com.greatyun.kakakopay.repository.*;
import com.greatyun.kakakopay.service.KakaoPayService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 뿌리기 조회 API 테스트
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class RestKakaoControllerSearchTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private KakaoPayService kakaoPayService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private MemberRoomMapRepository memberRoomMapRepository;

    @Autowired
    private MoneyRepository moneyRepository;

    @Autowired
    private CommonUtil commonUtil;

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

    @Test
    @DisplayName("뿌리기 조회 테스트")
    public void searchMoneyTest() throws Exception {
        // given
        int money = 10000;
        CreateMoneyDTO createMoneyDTO = CreateMoneyDTO.builder()
                .money(money)
                .peopleCnt(10)
                .build();
        // 뿌리기 등록
        String token = kakaoPayService.createShareMoney(member.getPkid(), chatRoom.getPkid(), createMoneyDTO);
        MemberRoomMap memberRoomMap = generateMemberChatRoomMapping(member, chatRoom);

        HttpHeaders headers = new HttpHeaders();
        headers.set(headerUserId , String.valueOf(member.getPkid()));
        headers.set(headerRoomId , String.valueOf(chatRoom.getPkid()));

        // 뿌리기 받기위한 토큰 설정
        RecvMoneyDTO recvMoneyDTO = new RecvMoneyDTO();
        recvMoneyDTO.setToken(token);

        mockMvc.perform(get("/api/v1.0/money/{token}" , token)
                .headers(headers)
            ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.money").exists())
                .andExpect(jsonPath("$.data.money").value(money))
                .andExpect(jsonPath("$.data.totalRecvMoney").exists())
                .andExpect(jsonPath("$.data.totalRecvMoney").value(0))
                .andExpect(jsonPath("$.data.recvList").exists())
                .andExpect(jsonPath("$.data.regDate").exists())
                ;

    }


    @Test
    @DisplayName("내가 생성한 방만 조회할 수 있다")
    public void searchMyMoneyTest() throws Exception {
        // given
        // 뿌리기 등록
        String token = generateMoney();
        MemberRoomMap memberRoomMap = generateMemberChatRoomMapping(member, chatRoom);

        Member newMember = generateMember();

        HttpHeaders headers = new HttpHeaders();
        headers.set(headerUserId , String.valueOf(newMember.getPkid()));
        headers.set(headerRoomId , String.valueOf(chatRoom.getPkid()));

        // 뿌리기 받기위한 토큰 설정
        RecvMoneyDTO recvMoneyDTO = new RecvMoneyDTO();
        recvMoneyDTO.setToken(token);

        mockMvc.perform(get("/api/v1.0/money/{token}" , token)
                .headers(headers)
        ).andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.code").value("-1"))
                .andExpect(jsonPath("$.message").exists())
        ;

    }


    @Test
    @DisplayName("뿌리기 조회는 7일간 가능하다")
    public void searchMoneyIn7DaysTest() throws Exception {
        // given
        // 뿌리기 등록

        MemberRoomMap memberRoomMap = generateMemberChatRoomMapping(member, chatRoom);
        Money money = Money.builder()
                .finishYn(EnumFinishYn.N)
                .peopleCnt(10)
                .money(10000)
                .token(commonUtil.createToken())
                .build();

        money.createMoney(member , chatRoom.getPkid());
        Money savedMoney = moneyRepository.save(money);

        // 날짜를 8일전 등록된 데이터로 변경
        savedMoney.changeDateTime(LocalDateTime.now().minusDays(8));
        moneyRepository.save(savedMoney);

        HttpHeaders headers = new HttpHeaders();
        headers.set(headerUserId , String.valueOf(member.getPkid()));
        headers.set(headerRoomId , String.valueOf(chatRoom.getPkid()));

        // 뿌리기 받기위한 토큰 설정
        RecvMoneyDTO recvMoneyDTO = new RecvMoneyDTO();
        recvMoneyDTO.setToken(savedMoney.getToken());

        mockMvc.perform(get("/api/v1.0/money/{token}" , savedMoney.getToken())
                .headers(headers)
        ).andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.code").value("-1"))
                .andExpect(jsonPath("$.message").exists())
        ;

    }

    // 뿌리기 등록
    private String generateMoney() throws Exception{
        // given
        CreateMoneyDTO createMoneyDTO = CreateMoneyDTO.builder()
                .money(10000)
                .peopleCnt(10)
                .build();
        // 뿌리기 등록
        return kakaoPayService.createShareMoney(member.getPkid(), chatRoom.getPkid(), createMoneyDTO);
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