package com.greatyun.kakakopay.controller.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class RecvMoneyResultDTO {

    @NotNull
    private int recvMoney;


}
