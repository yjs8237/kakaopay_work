package com.greatyun.kakakopay.domain;

import com.greatyun.kakakopay.domain.base.BaseEntity;
import com.greatyun.kakakopay.domain.dto.MemberDTO;
import lombok.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * 회원 엔티티
 */
@Entity
@Table(name = "tb_member")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Member extends BaseEntity {

    private String name;

    private String email;

    // 대화방 참여시 대화방 매핑 엔티티와 연관매핑
    @OneToMany(mappedBy = "member" , cascade = CascadeType.ALL)
    private List<MemberRoomMap> myRoomMapList;

    // 뿌리기 등록시 Money 엔티티와 연관매핑
    @OneToMany(mappedBy = "member" , cascade = CascadeType.ALL)
    private List<Money> myMoneyList;

    public void initMyRoomMapList() {
        if(this.myRoomMapList == null) {
            this.myRoomMapList = new ArrayList<>();
        }
    }


    public void initMyMoneyList() {
        if(this.myMoneyList == null) {
            this.myMoneyList = new ArrayList<>();
        }
    }

    public MemberDTO toDTO() {
        return MemberDTO.builder()
                .name(this.name)
                .email(this.email)
                .myMoneyList(this.myMoneyList)
                .myRoomMapList(this.myRoomMapList)
                .build();
    }
}
