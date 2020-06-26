package com.greatyun.kakakopay.domain;

import com.greatyun.kakakopay.domain.base.BaseEntity;
import com.greatyun.kakakopay.enumuration.EnumFinishYn;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

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

    @NotNull
    private Long roomId;

    // 뿌리기할 인원 수 (? 필요할까)
    private int peopleCnt;

    // 뿌리기 생성 후 발생하는 고유 토큰
    @NotNull
    private String token;

    // 뿌리기할 금액
    @NotNull
    @Min(1)
    private int money;

    @Enumerated(value = EnumType.STRING)
    @NotNull
    @Column(name = "finish_yn" , columnDefinition = "CHAR(1) default 'N'")
    private EnumFinishYn finishYn;

    // 뿌리기 받은 회원들의 결과 리스트
    @OneToMany(mappedBy = "money" , cascade = CascadeType.ALL)
    private List<MoneyResult> myMoneyResultList;


    // 뿌리기 한 회원
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fkMember")
    @NotNull
    private Member member;


    // 상태 업데이트
    public void changeFinishStatus(EnumFinishYn enumFinishYn) {
        this.finishYn = enumFinishYn;
    }

    // 뿌리기 엔티티 생성
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


    public void initMyMoneyResultList() {
        if(this.myMoneyResultList == null) {
            this.myMoneyResultList = new ArrayList<>();
        }
    }

}
