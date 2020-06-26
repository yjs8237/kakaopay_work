package com.greatyun.kakakopay.domain.dto;

import com.greatyun.kakakopay.domain.MemberRoomMap;
import com.greatyun.kakakopay.domain.Money;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberDTO {

    private String name;

    private String email;

    // 대화방 참여시 대화방 매핑 엔티티와 연관매핑
    private List<MemberRoomMap> myRoomMapList;

    // 뿌리기 등록시 Money 엔티티와 연관매핑
    private List<Money> myMoneyList;



}
