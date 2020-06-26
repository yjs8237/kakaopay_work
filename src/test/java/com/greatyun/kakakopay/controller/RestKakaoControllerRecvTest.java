package com.greatyun.kakakopay.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greatyun.kakakopay.common.CommonUtil;
import com.greatyun.kakakopay.controller.dto.CreateMoneyDTO;
import com.greatyun.kakakopay.controller.dto.RecvMoneyDTO;
import com.greatyun.kakakopay.domain.*;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 뿌리기 받기 API 테스트
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class RestKakaoControllerRecvTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private KakaoPayService kakaoPayService;

    @Autowired
    private ObjectMapper objectMapper;

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
    @DisplayName("뿌리기 받기 테스트")
    public void recvMoneyTest() throws Exception {
        // given
        CreateMoneyDTO createMoneyDTO = CreateMoneyDTO.builder()
                .money(10000)
                .peopleCnt(10)
                .build();
        // 뿌리기 등록
        String token = kakaoPayService.createShareMoney(member.getPkid(), chatRoom.getPkid(), createMoneyDTO);

        // 신규 회원 대화방 참여
        Member newMember = Member.builder()
                .build();
        Member savedMember = memberRepository.save(newMember);
        MemberRoomMap memberRoomMap = generateMemberChatRoomMapping(savedMember, chatRoom);

        HttpHeaders headers = new HttpHeaders();
        headers.set(headerUserId , String.valueOf(savedMember.getPkid()));
        headers.set(headerRoomId , String.valueOf(chatRoom.getPkid()));

        // 뿌리기 받기위한 토큰 설정
        RecvMoneyDTO recvMoneyDTO = new RecvMoneyDTO();
        recvMoneyDTO.setToken(token);

        mockMvc.perform(post("/api/v1.0/money/recieve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(recvMoneyDTO))
                .headers(headers)
            ).andDo(print())
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.recvMoney").exists())
                ;

    }

    @Test
    @DisplayName("뿌리기 생성한 사람은 받을 수 없다")
    public void recvMoneyFailTest() throws Exception {
        // given
        CreateMoneyDTO createMoneyDTO = CreateMoneyDTO.builder()
                .money(10000)
                .peopleCnt(10)
                .build();
        // 뿌리기 등록
        String token = kakaoPayService.createShareMoney(member.getPkid(), chatRoom.getPkid(), createMoneyDTO);


        HttpHeaders headers = new HttpHeaders();
        headers.set(headerUserId , String.valueOf(member.getPkid()));
        headers.set(headerRoomId , String.valueOf(chatRoom.getPkid()));

        // 뿌리기 받기위한 토큰 설정
        RecvMoneyDTO recvMoneyDTO = new RecvMoneyDTO();
        recvMoneyDTO.setToken(token);

        // when & then
        mockMvc.perform(post("/api/v1.0/money/recieve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(recvMoneyDTO))
                .headers(headers)
        ).andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.code").exists())
        ;
    }

    @Test
    @DisplayName("뿌리기 이후 10분뒤에는 받을 수 없다")
    public void recvMoneyTimeOverTest() throws Exception {

        Money money = Money.builder()
                .finishYn(EnumFinishYn.N)
                .peopleCnt(10)
                .money(10000)
                .token(commonUtil.createToken())
                .build();

        money.createMoney(member , chatRoom.getPkid());
        Money savedMoney = moneyRepository.save(money);

        // 신규 회원 대화방 참여
        Member newMember = Member.builder()
                .build();
        Member savedMember = memberRepository.save(newMember);

        // 시간을 10분전으로 강제 세팅
        savedMoney.changeDateTime(savedMoney.getRegDate().minusMinutes(11));
        MemberRoomMap memberRoomMap = generateMemberChatRoomMapping(savedMember, chatRoom);

        HttpHeaders headers = new HttpHeaders();
        // 신규회원으로 받기 시도
        headers.set(headerUserId , String.valueOf(newMember.getPkid()));
        headers.set(headerRoomId , String.valueOf(chatRoom.getPkid()));

        // 뿌리기 받기위한 토큰 설정
        RecvMoneyDTO recvMoneyDTO = new RecvMoneyDTO();
        recvMoneyDTO.setToken(savedMoney.getToken());

        // when & then
        mockMvc.perform(post("/api/v1.0/money/recieve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(recvMoneyDTO))
                .headers(headers)
        ).andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").exists())
        ;
    }

    @Test
    @DisplayName("받는 것은 자신이 속한 대화방 뿌리기만 가능하다")
    public void recvMoneyAcceptTest() throws Exception {

        String token = generateMoney();

        // 신규 회원 대화방 참여
        Member newMember = Member.builder()
                .build();
        Member savedMember = memberRepository.save(newMember);

        ChatRoom newChatRoom = ChatRoom.builder()
                .roomName("new")
                .ownerMemberId(savedMember.getPkid())
                .build();
        ChatRoom savedRoom = chatRoomRepository.save(newChatRoom);

        MemberRoomMap memberRoomMap = generateMemberChatRoomMapping(savedMember, savedRoom);

        HttpHeaders headers = new HttpHeaders();
        // 신규회원으로 받기 시도
        headers.set(headerUserId , String.valueOf(newMember.getPkid()));
        headers.set(headerRoomId , String.valueOf(chatRoom.getPkid()));

        // 뿌리기 받기위한 토큰 설정
        RecvMoneyDTO recvMoneyDTO = new RecvMoneyDTO();
        recvMoneyDTO.setToken(token);

        // when & then
        mockMvc.perform(post("/api/v1.0/money/recieve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(recvMoneyDTO))
                .headers(headers)
        ).andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.code").exists())
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