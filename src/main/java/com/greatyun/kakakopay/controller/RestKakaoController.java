package com.greatyun.kakakopay.controller;

import com.greatyun.kakakopay.common.API_RESULT_MSG;
import com.greatyun.kakakopay.controller.dto.*;
import com.greatyun.kakakopay.service.KakaoPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1.0")
@Slf4j
public class RestKakaoController {

    @Value("${kakaopay.user.id}")
    private String headerUserId;

    @Value("${kakaopay.room.id}")
    private String headerRoomId;

    @Autowired
    private KakaoPayService kakaoPayService;

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
                            , @RequestHeader HttpHeaders headers)  {

        // Reqeust Body 체크
        if (checkReqeustBody(errors)) return getValidationError(errors);

        List<String> userHeaders = headers.get(headerUserId);
        List<String> roomHeaders = headers.get(headerRoomId);
        if (checkRequestHeader(userHeaders, roomHeaders))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorDTO("Bad request header"));

        // 뿌리기 성공 후 response Token
        String token = "";

        try {
            long memberId = Long.parseLong(userHeaders.get(0) == null ? "0" : userHeaders.get(0));
            long roomId = Long.parseLong(roomHeaders.get(0) == null ? "0" : roomHeaders.get(0));
            // 뿌리기 생성
            token = kakaoPayService.createShareMoney(memberId , roomId , paramDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getErrorDTO(e.getLocalizedMessage()));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(getSuccessDTO(CreateMoneyResultDTO.builder().token(token).build()));
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
            , @RequestHeader HttpHeaders headers)  {

        // Reqeust Body 체크
        if (checkReqeustBody(errors)) return getValidationError(errors);

        List<String> userHeaders = headers.get(headerUserId);
        List<String> roomHeaders = headers.get(headerRoomId);
        // 헤더 정보가 올바르지 않으면..
        if (checkRequestHeader(userHeaders, roomHeaders))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorDTO("Bad request header"));

        int recvMoney = 0;
        try {
            long memberId = Long.parseLong(userHeaders.get(0) == null ? "0" : userHeaders.get(0));
            long roomId = Long.parseLong(roomHeaders.get(0) == null ? "0" : roomHeaders.get(0));
            recvMoney = kakaoPayService.recvShareMoney(memberId , roomId , paramDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getErrorDTO(e.getLocalizedMessage()));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(getSuccessDTO(RecvMoneyResultDTO.builder().recvMoney(recvMoney).build()));

    }

    /**
     * 조회 API
     * @param paramDTO
     * @param errors
     * @param headers
     * @return
     */
    @GetMapping("/money")
    public ResponseEntity searchMoney(@RequestBody @Valid SearchMoneyDTO paramDTO
            , Errors errors
            , @RequestHeader HttpHeaders headers)  {

        // Reqeust Body 체크
        if (checkReqeustBody(errors)) return getValidationError(errors);

        List<String> userHeaders = headers.get(headerUserId);
        List<String> roomHeaders = headers.get(headerRoomId);
        // 헤더 정보가 올바르지 않으면..
        if (checkRequestHeader(userHeaders, roomHeaders))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorDTO("Bad request header"));

        SearchMoneyResultDTO resultDTO = null;
        try {
            long memberId = Long.parseLong(userHeaders.get(0) == null ? "0" : userHeaders.get(0));
            long roomId = Long.parseLong(roomHeaders.get(0) == null ? "0" : roomHeaders.get(0));
            resultDTO = kakaoPayService.searchShareMoney(memberId, roomId, paramDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getErrorDTO(e.getLocalizedMessage()));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(getSuccessDTO(resultDTO));
    }

    private boolean checkRequestHeader(List<String> userHeaders, List<String> roomHeaders) {
        // 헤더 정보가 올바르지 않으면..
        if (userHeaders.size() == 0 || roomHeaders.size() == 0) {
            return true;
        }
        return false;
    }

    private boolean checkReqeustBody(Errors errors) {
        if (errors.hasErrors()) {
            return true;
        }
        return false;
    }


    /**
     * Request Body Validation 체크 에러  DTO
     * @param errors
     * @return
     */
    private ResponseEntity getValidationError(Errors errors) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getBindingError(errors));
    }

    /**
     * API Error DTO
     * @param localizedMessage
     * @return
     */
    private ApiResultDTO getErrorDTO(String localizedMessage) {
        return ApiResultDTO.builder()
                .code(-1)
                .message(localizedMessage)
                .build();
    }

    private ApiResultDTO getSuccessDTO(Object data) {
        return ApiResultDTO.builder()
                .code(0)
                .message("완료되었습니다.")
                .data(data)
                .build();
    }

    /**
     * Binding Error 필드 생성
     * @param errors
     * @return
     */
    public  ApiResultDTO getBindingError (Errors errors) {
        List<FieldError> fieldErrors = errors.getFieldErrors();
        ApiErrorDTO apiErrorDTO = new ApiErrorDTO();
        apiErrorDTO.setCode(-1);
        apiErrorDTO.setMessage(API_RESULT_MSG.API_ERR_BINDING);

        for (FieldError error : fieldErrors) {
            ErrorDTO errorDTO = ErrorDTO.builder()
                    .field(error.getField())
                    .message(error.getDefaultMessage())
                    .build();
            apiErrorDTO.getErrors().add(errorDTO);
        }
        return apiErrorDTO;
    }


}
