package com.greatyun.kakakopay;

import com.greatyun.kakakopay.exception.KakaoPayException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class KakaoPayService {

    /**
     * 뿌리기 등록
     * @throws KakaoPayException
     */
    @Transactional(rollbackFor = KakaoPayException.class)
    public void createShareMoney () throws KakaoPayException {

    }

}
