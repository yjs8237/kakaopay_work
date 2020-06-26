package com.greatyun.kakakopay.exception;

public class KakaoPayException extends Exception {

    private String reason;

    public KakaoPayException(String reason) {
        super(reason);
        this.reason = reason;
    }

    @Override
    public String getMessage() {
        return this.reason;
    }

}
