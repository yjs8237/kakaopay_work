package com.greatyun.kakakopay.domain;

import com.greatyun.kakakopay.domain.base.BaseEntity;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * 뿌리기 엔티티
 * 회원이 뿌리기를 시도할때 관리할 수 있는 뿌리기 Master
 */
@Entity
@Table(name = "tb_money")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Money extends BaseEntity {

    private Long roomId;

    // 뿌리기할 인원 수 (? v필요할까)
    private int peopleCnt;

    // 뿌리기할 금액
    private int money;

    // 뿌리기 한 회원
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fkMember")
    @NotNull
    private Member member;


    public void createMoney(Member member , Long roomId) {
        this.setMember(member);
        this.roomId = roomId;
    }

    private void setMember(Member member) {
        if(this.member != null) {
            this.member.getMyMoneyList().remove(this);
        }
        this.member = member;
        if(this.member.getMyMoneyList() == null) {
            this.member.initMyMoneyList();
        }
        this.member.getMyMoneyList().add(this);
    }

}
