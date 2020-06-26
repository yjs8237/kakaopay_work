package com.greatyun.kakakopay.domain;

import com.greatyun.kakakopay.domain.base.BaseEntity;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * 대화방 엔티티
 */
@Entity
@Table(name = "tb_room")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ChatRoom extends BaseEntity {

    // 대화방을 생성한 멤버 ID (방장)
    @Column(name = "owner_member_id")
    @NotNull
    private Long ownerMemberId;

    private String roomName;

    // 뿌리기 등록시 Money 엔티티와 연관매핑
    @OneToMany(mappedBy = "chatRoom" , cascade = CascadeType.ALL)
    private List<MemberRoomMap> myRoomMapList;

    public void initMyRoomMapList() {
        if(this.myRoomMapList == null) {
            this.myRoomMapList = new ArrayList<>();
        }
    }
}
