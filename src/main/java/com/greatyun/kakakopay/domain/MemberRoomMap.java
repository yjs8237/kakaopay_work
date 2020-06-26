package com.greatyun.kakakopay.domain;

import com.greatyun.kakakopay.domain.base.BaseEntity;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * 회원과 대화방 매핑 엔티티
 */
@Entity
@Table(name = "tb_member_room_map")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class MemberRoomMap extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fkMember")
    @NotNull
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fkRoom")
    @NotNull
    private ChatRoom chatRoom;

    /**
     * 대화방과 회원을 매핑
     * @param member
     * @param chatRoom
     */
    public void joinRoomAndMemberMap(Member member , ChatRoom chatRoom) {
        this.setMember(member);
        this.setChatRoom(chatRoom);
    }

    private void setMember(Member member) {
        if(this.member != null) {
            this.member.getMyRoomMapList().remove(this);
        }
        this.member = member;
        if(this.member.getMyRoomMapList() == null) {
            this.member.initMyRoomMapList();
        }
        this.member.getMyRoomMapList().add(this);
    }

    private void setChatRoom(ChatRoom chatRoom) {
        if(this.chatRoom != null) {
            this.chatRoom.getMyRoomMapList().remove(this);
        }
        this.chatRoom = chatRoom;
        if(this.chatRoom.getMyRoomMapList() == null) {
            this.chatRoom.initMyRoomMapList();
        }
        this.chatRoom.getMyRoomMapList().add(this);
    }
}
