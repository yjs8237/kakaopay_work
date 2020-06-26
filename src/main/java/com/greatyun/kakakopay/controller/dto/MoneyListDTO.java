package com.greatyun.kakakopay.controller.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MoneyListDTO {

   // 받은 금액
   private int recvMoney;

   // 받은 사용자 아이디
   private Long recvMemberId;

}
