package com.greatyun.kakakopay.controller.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;

@Data
@Builder
public class CreateMoneyResultDTO {

    private String token;
}
