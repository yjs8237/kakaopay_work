package com.greatyun.kakakopay.service;

import com.greatyun.kakakopay.common.API_RESULT_MSG;
import com.greatyun.kakakopay.common.CommonUtil;
import com.greatyun.kakakopay.controller.dto.*;
import com.greatyun.kakakopay.domain.*;
import com.greatyun.kakakopay.enumuration.EnumFinishYn;
import com.greatyun.kakakopay.exception.KakaoPayException;
import com.greatyun.kakakopay.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
@Slf4j
public class KakaoPayService {

    private final MemberRepository memberRepository;

    private final MoneyRepository moneyRepository;

    private final MoneyResultRepository moneyResultRepository;

    private final ChatRoomRepository chatRoomRepository;

    private final MemberRoomMapRepository memberRoomMapRepository;

    private final CommonUtil commonUtil;

    /**
     * 뿌리기 등록
     * @throws KakaoPayException
     */
    @Transactional(rollbackFor = KakaoPayException.class)
    public String createShareMoney (Long memberId , Long roomId , CreateMoneyDTO paramDTO) throws KakaoPayException {
        // 1. 회원 유무 체크
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new KakaoPayException(API_RESULT_MSG.API_ERR_MEMBER_NOT_EXIST));

        // 2. 대화방 체크
        MemberRoomMap memberRoomMap = memberRoomMapRepository.findByMember_PkidAndChatRoom_Pkid(memberId, roomId)
                .orElseThrow(() -> new KakaoPayException(API_RESULT_MSG.API_ERR_CHAT_ROOM_NOT_VALID));

        // 토큰 발행
        String token = commonUtil.createToken();
        Money money = Money.builder()
                .finishYn(EnumFinishYn.N)
//                .peopleCnt(paramDTO.getPeopleCnt())
                .money(paramDTO.getMoney())
                .token(token)
                .build();
        money.createMoney(member , memberRoomMap.getChatRoom().getPkid());
        Money savedMoney = moneyRepository.save(money);

        // 토큰 발행하고 분배 금액을 정하자
        List<Integer> list = commonUtil.divideMoneyRandomly(paramDTO.getPeopleCnt(), paramDTO.getMoney());
        for (Integer inMoney : list) {
            MoneyResult moneyResult = MoneyResult.builder()
                    .finishYn(EnumFinishYn.N)
                    .recvMoney(inMoney)
                    .money(savedMoney)
                    .build();
            moneyResultRepository.save(moneyResult);
        }
        return token;
    }

    /**
     * 뿌리기 금액 받기
     * @param memberId
     * @param roomId
     * @param paramDTO
     * @return
     * @throws KakaoPayException
     */
    @Transactional(rollbackFor = KakaoPayException.class)
    public int recvShareMoney (Long memberId , Long roomId , RecvMoneyDTO paramDTO) throws KakaoPayException {
        // 1. 회원 유무 체크
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new KakaoPayException(API_RESULT_MSG.API_ERR_MEMBER_NOT_EXIST));

        // 2. 요청 회원 해당 대화방 참석 회원인지 체크
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new KakaoPayException(API_RESULT_MSG.API_ERR_CHAT_ROOM_NOT_VALID));
        MemberRoomMap memberRoomMap = memberRoomMapRepository.findByMember_PkidAndChatRoom_Pkid(member.getPkid(), chatRoom.getPkid())
                .orElseThrow(() -> new KakaoPayException(API_RESULT_MSG.API_ERR_CHAT_ROOM_NOT_IN));


        // 3. 뿌리기 데이터 확인
        Money money = moneyRepository.findByRoomIdAndToken(roomId, paramDTO.getToken())
                .orElseThrow(() -> new KakaoPayException(API_RESULT_MSG.API_ERR_CHAT_ROOM_NOT_MONEY));
        if(money.getMember().getPkid() == member.getPkid()) {
            // 발송 본인은 받을 수 없다.
            throw new KakaoPayException(API_RESULT_MSG.API_ERR_MONEY_CANNOT);
        }
        if(money.getFinishYn().compareTo(EnumFinishYn.Y) == 0) {
            // 이미 완료되었는지 체크
            throw new KakaoPayException(API_RESULT_MSG.API_ERR_MONEY_ALREADY_FINISH);
        }

        // 4. 시간 체크 10분이 지난 뿌리기는 받을 수 없다
        LocalDateTime regDate = money.getRegDate();
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(regDate , now);
        long durationMinute = (duration.getSeconds() / 60) * -1;
        if(durationMinute > 10) {
            // 10분이 지난 뿌리기 건이라면 받을 수 없다.
            throw new KakaoPayException(API_RESULT_MSG.API_ERR_MONEY_TIME_OVER);
        }

        // 5. 이미 받기가 완료된 고객인지 체크
        Optional<MoneyResult> optionalMoney = moneyResultRepository.findByMoneyAndRecvMemberId(money, member.getPkid());
        if(optionalMoney.isPresent()) {
            // 존재한다면 받을 수 없다.
            throw new KakaoPayException(API_RESULT_MSG.API_ERR_MONEY_ALREADY_RECV);
        }

        // 페이징 처리로 하자 어차피 한건만 가져오면 되니까..
        // 뿌리기 인원 수 가 많을 경우 메모리 부담..
        Pageable pageable = PageRequest.of(0 , 10);
        Page<MoneyResult> resultPage = moneyResultRepository.findAllByMoney_PkidAndFinishYn(money.getPkid(), EnumFinishYn.N, pageable);

        // 조회 결과가 없다면 완료 상태로 업데이트..
        if(resultPage.getTotalElements() == 0) {
            // 이미 완료된 받기 건일 경우
            throw new KakaoPayException(API_RESULT_MSG.API_ERR_MONEY_ALREADY_FINISH);
        }
        // 해당 뿌리기 건에 대해 받기 완료처리 하고
        MoneyResult moneyResult = resultPage.getContent().get(0);
        moneyResult.recvMoney(member.getPkid());

        if(resultPage.getTotalElements() == 1) {
            // 마지막 남은 받기 건이였다면 완료 업데이트하자..
            money.changeFinishStatus(EnumFinishYn.Y);
        }

        // 받은 금액을 리턴하자..
        return moneyResult.getRecvMoney();
    }


    /**
     * 조회 API
     * @param memberId
     * @param roomId
     * @param token
     * @return
     * @throws KakaoPayException
     */
    @Transactional(rollbackFor = KakaoPayException.class)
    public SearchMoneyResultDTO searchShareMoney (Long memberId , Long roomId , String token) throws KakaoPayException {

        // 1. 회원 유무 체크.
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new KakaoPayException(API_RESULT_MSG.API_ERR_MEMBER_NOT_EXIST));

        // 2. 요청 대화방 생성 회원인지 체크
        Money money = moneyRepository.findByRoomIdAndTokenAndMember(roomId, token, member)
                .orElseThrow(() -> new KakaoPayException(API_RESULT_MSG.API_ERR_MEMBER_NOT_PERMIT));

        // 3. 조회는 7일 동안 유효하다
        LocalDate regDate = money.getRegDate().toLocalDate();
        LocalDate now = LocalDate.now();
        Period period = regDate.until(now);

        log.info("## regDate -> " + regDate.toString());
        log.info("## period -> " + period.getDays());

        if(period.getDays() > 7) {
            throw new KakaoPayException(API_RESULT_MSG.API_ERR_SEARCH_IN_7DAYS);
        }

        List<MoneyResult> resultList = moneyResultRepository.findAllByMoneyAndFinishYn(money , EnumFinishYn.Y);
        int totalRecvMoney = resultList.stream().mapToInt(x -> x.getRecvMoney()).sum();

        return SearchMoneyResultDTO.builder()
                .money(money.getMoney())
                .regDate(money.getRegDate())
                .totalRecvMoney(totalRecvMoney)
                .recvList(resultList.stream().map(MoneyResult::toSearchDTO).collect(Collectors.toList()))
                .build();
    }



}
