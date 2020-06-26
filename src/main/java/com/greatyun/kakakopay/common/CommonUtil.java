package com.greatyun.kakakopay.common;

import com.greatyun.kakakopay.exception.KakaoPayException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Component
public class CommonUtil {
    /**
     * 뿌리기 하기 위한 랜덤 금액을 구하는 함수
     * @param peopleCnt
     * @param shareMoney
     * @return 인원수에 맞는 랜덤한 금액을 포함하는 List 리턴
     * @throws KakaoPayException
     */
    public List<Integer> divideMoneyRandomly(int peopleCnt , int shareMoney) throws KakaoPayException {
        List<Integer> ret = new ArrayList<>();

        if(peopleCnt == 0 || shareMoney == 0) {
            throw new KakaoPayException("인원수와 뿌리기 금액은 0이 될 수 없습니다.");
        }

        // 인원이 1명이면 뿌리기 금액 전체를 가져간다
        if(peopleCnt == 1) {
            ret.add(shareMoney);
            return ret;
        }

        Random random = new Random();
        int divideTargetCnt = peopleCnt;
        for (int i = 0; i < peopleCnt; i++) {
            if(i == (peopleCnt - 1)) {
                ret.add(shareMoney);
                break;
            }
            int bound = shareMoney / divideTargetCnt;
            if(bound <= 0) {
                ret.add(0);
            } else {
                int randomMoney = shareMoney <= 0 ? 0 : random.nextInt(bound);
                ret.add(randomMoney);
                divideTargetCnt--;
                shareMoney -= randomMoney;
                shareMoney = shareMoney < 0 ? 0 : shareMoney;
            }
        }
        return ret;
    }

    /**
     * 뿌리기 완료 후 토큰 생성
     *
     */
    public String createToken() {
        String ret = UUID.randomUUID().toString();
        // 뿌리기 생성 후 고유한 token 을 만들기 위한 UUID 사용
        ret = ret.replaceAll("-" , "").substring(0 , 3);
        return ret;
    }

}
