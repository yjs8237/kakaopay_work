package com.greatyun.kakakopay.controller.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ApiErrorDTO extends ApiResultDTO {

    private List<ErrorDTO> errors = new ArrayList<>();
}
