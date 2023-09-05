package com.cq.common.response;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用响应类
 *
 * @author 程崎
 * @since 2023/07/29
 */
@Data
public class CommonResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private int code;

    private T data;

    private String msg;

    private CommonResponse(int code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    private CommonResponse() {
    }

    public static <T> CommonResponse<T> success(T data) {
        return CommonResponse.build(0, data, "ok");
    }

    public static <T> CommonResponse<T> success(T data, String msg) {
        return CommonResponse.build(0, data, msg);
    }
    public static <T> CommonResponse<T> error(ResultCodeEnum resultCodeEnum) {
        return CommonResponse.build(resultCodeEnum.getCode(), null, resultCodeEnum.getMessage());
    }
    public static <T> CommonResponse<T> error(ResultCodeEnum resultCodeEnum,String msg) {
        return CommonResponse.build(resultCodeEnum.getCode(), null, msg);
    }

    public static <T> CommonResponse<T> error(T data, ResultCodeEnum resultCodeEnum) {
        return CommonResponse.build(resultCodeEnum.getCode(), data, resultCodeEnum.getMessage());
    }

    public static <T> CommonResponse<T> build(int code, T data, String msg) {
        return new CommonResponse<>(code, data, msg);
    }


}
