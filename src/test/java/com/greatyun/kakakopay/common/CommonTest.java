package com.greatyun.kakakopay.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("공통 비지니스 로직 테스트")
public class CommonTest {

    // 뿌릴 금액
    private int shareMoney;

    // 금액을 나눠줄 인원 수
    private int peopleCnt;

    @Autowired
    private CommonUtil commonUtil;

    @BeforeEach
    public void before() {
        this.shareMoney = 5;
        this.peopleCnt = 10;
    }

    @Test
    @DisplayName("정해진 인원수와 정해진 금액을 랜덤으로 나눠준 합의 금액은 동일해야 한다")
    public void shareMoneyTest() throws Exception {

        List<Integer> result = commonUtil.divideMoneyRandomly(peopleCnt, shareMoney);

        int total = 0;
        for (Integer money : result) {
            //System.out.println("list mon -> " + money);
            total += money;
        }

        System.out.println("total : " + total + " , shareMoney : "+ shareMoney);
        // 뿌리려는 금액과 인원수에 맞게 랜덤하게 책정된 금액의 합계는 동일해야 한다.
        assertThat(total).isEqualTo(shareMoney);
    }


}
