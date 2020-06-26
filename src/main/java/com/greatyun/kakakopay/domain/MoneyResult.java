package com.greatyun.kakakopay.domain;

import com.greatyun.kakakopay.controller.dto.MoneyListDTO;
import com.greatyun.kakakopay.controller.dto.SearchMoneyResultDTO;
import com.greatyun.kakakopay.domain.base.BaseEntity;
import com.greatyun.kakakopay.enumuration.EnumFinishYn;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 뿌리기 금액 받은 결과를 나타내는 엔티티
 */
@Entity
@Table(name = "tb_money_result")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class MoneyResult extends BaseEntity {

    // 뿌리기를 받은 유저의 id
    private Long recvMemberId;

    // 뿌리기 받은 금액
    @NotNull
    @Min(0)
    private int recvMoney;

    // 뿌리기 받았는지 여부
    @Enumerated(value = EnumType.STRING)
    @NotNull
    @Column(name = "finish_yn" , columnDefinition = "CHAR(1) default 'N'")
    private EnumFinishYn finishYn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fkMoney")
    @NotNull
    private Money money;


    // 받기 완료 하기
    public void recvMoney(Long recvMemberId) {
        this.recvMemberId = recvMemberId;
        this.finishYn = EnumFinishYn.Y;
    }

    private void setMoney(Money money) {
        if(this.money != null) {
            this.money.getMyMoneyResultList().remove(this);
        }
        this.money = money;
        if(this.money.getMyMoneyResultList() == null) {
            this.money.initMyMoneyResultList();
        }
        this.money.getMyMoneyResultList().add(this);
    }

    public MoneyListDTO toSearchDTO() {
        return MoneyListDTO.builder()
                .recvMemberId(this.recvMemberId)
                .recvMoney(this.recvMoney)
                .build();
    }

}
