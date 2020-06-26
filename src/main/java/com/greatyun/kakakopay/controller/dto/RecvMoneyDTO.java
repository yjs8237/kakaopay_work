package com.greatyun.kakakopay.controller.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RecvMoneyDTO {

    @NotNull
    private String token;


}
