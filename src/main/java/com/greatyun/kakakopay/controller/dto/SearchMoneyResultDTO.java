package com.greatyun.kakakopay.controller.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class SearchMoneyResultDTO {

   // 뿌린 시각
   private LocalDateTime regDate;

   // 뿌린 금액
   private int money;

   // 받기 완료된금액
   private int totalRecvMoney;

   // 받은 사용자 정보 리스트
   List<MoneyListDTO> recvList;
   
}
