package com.greatyun.kakakopay.common;

import com.greatyun.kakakopay.exception.KakaoPayException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class KakaoExceptionHandler {

    @ExceptionHandler(KakaoPayException.class)
    protected ResponseEntity handleKakaoException (KakaoPayException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResultUtil.getErrorDTO(e.getLocalizedMessage()));
    }

}
