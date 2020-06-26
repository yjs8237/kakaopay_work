package com.greatyun.kakakopay.controller;

import com.greatyun.kakakopay.common.API_RESULT_MSG;
import com.greatyun.kakakopay.common.ResultUtil;
import com.greatyun.kakakopay.controller.dto.*;
import com.greatyun.kakakopay.service.KakaoPayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1.0")
@Slf4j
public class RestKakaoController {

    @Value("${kakaopay.user.id}")
    private String headerUserId;

    @Value("${kakaopay.room.id}")
    private String headerRoomId;

    private final KakaoPayService kakaoPayService;

    /**
     * 뿌리기 API
     * @param paramDTO
     * @param errors
     * @param headers
     * @return
     */
    @PostMapping("/money")
    public ResponseEntity createMoney(@RequestBody @Valid CreateMoneyDTO paramDTO
                            , Errors errors
                            , @RequestHeader HttpHeaders headers) throws Exception {

        // Reqeust Body 체크
        if (checkReqeustBody(errors)) return ResultUtil.getValidationError(errors);

        List<String> userHeaders = headers.get(headerUserId);
        List<String> roomHeaders = headers.get(headerRoomId);
        if (checkRequestHeader(userHeaders, roomHeaders))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResultUtil.getErrorDTO(API_RESULT_MSG.API_BAD_REQUEST_HEADER));

        // 뿌리기 성공 후 response Token
        String token = "";

        long memberId = Long.parseLong(userHeaders.get(0) == null ? "0" : userHeaders.get(0));
        long roomId = Long.parseLong(roomHeaders.get(0) == null ? "0" : roomHeaders.get(0));
        // 뿌리기 생성
        token = kakaoPayService.createShareMoney(memberId, roomId, paramDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(ResultUtil.getSuccessDTO(CreateMoneyResultDTO.builder().token(token).build()));
    }



    /**
     * 받기 API
     * @param paramDTO
     * @param errors
     * @param headers
     * @return
     */
    @PostMapping("/money/recieve")
    public ResponseEntity recvMoney(@RequestBody @Valid RecvMoneyDTO paramDTO
            , Errors errors
            , @RequestHeader HttpHeaders headers) throws Exception {

        // Reqeust Body 체크
        if (checkReqeustBody(errors)) return ResultUtil.getValidationError(errors);

        List<String> userHeaders = headers.get(headerUserId);
        List<String> roomHeaders = headers.get(headerRoomId);
        // 헤더 정보가 올바르지 않으면..
        if (checkRequestHeader(userHeaders, roomHeaders))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResultUtil.getErrorDTO(API_RESULT_MSG.API_BAD_REQUEST_HEADER));

        int recvMoney = 0;
        long memberId = Long.parseLong(userHeaders.get(0) == null ? "0" : userHeaders.get(0));
        long roomId = Long.parseLong(roomHeaders.get(0) == null ? "0" : roomHeaders.get(0));
        recvMoney = kakaoPayService.recvShareMoney(memberId, roomId, paramDTO);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(ResultUtil.getSuccessDTO(RecvMoneyResultDTO.builder().recvMoney(recvMoney).build()));

    }

    /**
     * 조회 API
     * @param token
     * @param headers
     * @return
     */
    @GetMapping("/money/{token}")
    public ResponseEntity searchMoney(@PathVariable(value = "token") String token
            , @RequestHeader HttpHeaders headers) throws Exception {

        if(!StringUtils.hasText(token)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResultUtil.getErrorDTO(API_RESULT_MSG.API_BAD_REQUEST_PARAM));
        }

        List<String> userHeaders = headers.get(headerUserId);
        List<String> roomHeaders = headers.get(headerRoomId);
        // 헤더 정보가 올바르지 않으면..
        if (checkRequestHeader(userHeaders, roomHeaders))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResultUtil.getErrorDTO(API_RESULT_MSG.API_BAD_REQUEST_HEADER));

        SearchMoneyResultDTO resultDTO = null;
        long memberId = Long.parseLong(userHeaders.get(0) == null ? "0" : userHeaders.get(0));
        long roomId = Long.parseLong(roomHeaders.get(0) == null ? "0" : roomHeaders.get(0));

        log.info("memberId : " + memberId);
        log.info("roomId : " + roomId);

        resultDTO = kakaoPayService.searchShareMoney(memberId, roomId, token);

        return ResponseEntity.status(HttpStatus.OK).body(ResultUtil.getSuccessDTO(resultDTO));
    }

    /**
     * 헤더 정보 체크 메소드
     * @param userHeaders
     * @param roomHeaders
     * @return
     */
    private boolean checkRequestHeader(List<String> userHeaders, List<String> roomHeaders) {
        // 헤더 정보가 올바르지 않으면..
        if (userHeaders.size() == 0 || roomHeaders.size() == 0) {
            return true;
        }
        return false;
    }

    /**
     * Request Body Valid 체크
     * @param errors
     * @return
     */
    private boolean checkReqeustBody(Errors errors) {
        if (errors.hasErrors()) {
            return true;
        }
        return false;
    }



}
