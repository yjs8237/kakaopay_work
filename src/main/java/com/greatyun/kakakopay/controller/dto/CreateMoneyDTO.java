package com.greatyun.kakakopay.controller.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class CreateMoneyDTO {

    @Min(1)
    private int peopleCnt;

    @Min(1)
    private int money;
}
