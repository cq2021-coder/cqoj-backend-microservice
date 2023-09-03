package com.cq.common.exception;

import com.cq.common.response.CommonResponse;
import com.cq.common.response.ResultCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * @author 程崎
 * @since 2023/07/29
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public CommonResponse<?> businessExceptionHandler(BusinessException e) {
        log.info("BusinessException", e);
        if (e.getMessage() != null) {
            return CommonResponse.error(e.getResultCodeEnum(), e.getMessage());
        }
        return CommonResponse.error(e.getResultCodeEnum());
    }

    @ExceptionHandler(value = BindException.class)
    public CommonResponse<Object> validExceptionHandler(BindException e) {
        String errorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.warn("parameter validation failed:{}", errorMessage);
        return CommonResponse.error(ResultCodeEnum.PARAMS_ERROR, errorMessage);
    }


    @ExceptionHandler(RuntimeException.class)
    public CommonResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("RuntimeException", e);
        return CommonResponse.error(ResultCodeEnum.SYSTEM_ERROR);
    }
}
