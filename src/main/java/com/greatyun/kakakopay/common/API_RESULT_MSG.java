package com.greatyun.kakakopay.common;

public interface API_RESULT_MSG {

    public static final String API_ERR_BINDING = "Bad Request body data";

    public static final String API_BAD_REQUEST_HEADER = "Bad request header";

    public static final String API_BAD_REQUEST_PARAM = "Bad request parameter";



    public static final String API_ERR_MEMBER_NOT_EXIST = "존재하지 않는 사용자 입니다.";

    public static final String API_ERR_MEMBER_NOT_PERMIT = "요청하신 대화방은 고객님은 조회 하실 수 없습니다.";

    public static final String API_ERR_CHAT_ROOM_NOT_VALID = "올바르지 않은 대화방 입니다.";

    public static final String API_ERR_CHAT_ROOM_NOT_IN = "고객님은 요청하신 대화방에 참여하고 있지 않습니다.";

    public static final String API_ERR_CHAT_ROOM_NOT_MONEY = "요청하신 대화방은 뿌리기가 존재하지 않습니다.";

    public static final String API_ERR_MONEY_CANNOT = "본인이 발송한 뿌리기는 받을 수 없습니다.";

    public static final String API_ERR_MONEY_ALREADY_FINISH = "이미 받기가 완료된 뿌리기 입니다.";

    public static final String API_ERR_MONEY_TIME_OVER = "10분이 지난 뿌리기는 받을 수 없습니다.";

    public static final String API_ERR_MONEY_ALREADY_RECV = "고객님은 이미 받기가 완료된 상태 입니다.";

    public static final String API_ERR_SEARCH_IN_7DAYS = "뿌리기 조회는 최대 7일동안 유효 합니다.";









}
