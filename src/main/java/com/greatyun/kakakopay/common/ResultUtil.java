package com.greatyun.kakakopay.common;

import com.greatyun.kakakopay.controller.dto.ApiErrorDTO;
import com.greatyun.kakakopay.controller.dto.ApiResultDTO;
import com.greatyun.kakakopay.controller.dto.ErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.List;

public  class ResultUtil {

    /**
     * Request Body Validation 체크 에러  DTO
     * @param errors
     * @return
     */
    public static ResponseEntity getValidationError(Errors errors) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getBindingError(errors));
    }

    /**
     * API Error DTO
     * @param localizedMessage
     * @return
     */
    public static ApiResultDTO getErrorDTO(String localizedMessage) {
        return ApiResultDTO.builder()
                .code(-1)
                .message(localizedMessage)
                .build();
    }

    /**
     * 성공 Response DTO
     * @param data
     * @return
     */
    public static ApiResultDTO getSuccessDTO(Object data) {
        return ApiResultDTO.builder()
                .code(0)
                .message("완료되었습니다.")
                .data(data)
                .build();
    }

    /**
     * Binding Error 필드 생성
     * @param errors
     * @return
     */
    public  static ApiResultDTO getBindingError (Errors errors) {
        List<FieldError> fieldErrors = errors.getFieldErrors();
        ApiErrorDTO apiErrorDTO = new ApiErrorDTO();
        apiErrorDTO.setCode(-1);
        apiErrorDTO.setMessage(API_RESULT_MSG.API_ERR_BINDING);

        for (FieldError error : fieldErrors) {
            ErrorDTO errorDTO = ErrorDTO.builder()
                    .field(error.getField())
                    .message(error.getDefaultMessage())
                    .build();
            apiErrorDTO.getErrors().add(errorDTO);
        }
        return apiErrorDTO;
    }


}
