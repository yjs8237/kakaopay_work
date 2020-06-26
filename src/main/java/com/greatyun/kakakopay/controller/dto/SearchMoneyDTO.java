package com.greatyun.kakakopay.controller.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class SearchMoneyDTO {

   @NotNull
   private String token;

}
