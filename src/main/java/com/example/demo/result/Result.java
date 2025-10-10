package com.example.demo.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;


@Data
public class Result<T> implements Serializable {

    /**
     * 1成功 0失败
     */
    @Schema(description = "响应代码")
    private Integer code;

    @Schema(description = "返回信息")
    private String msg;

    @Schema(description = "返回数据")
    private T data;

    public static <T> Result<T> success() {
        Result<T> result = new Result<T>();
        result.code = 1;
        return result;
    }

    public static <T> Result<T> success(T object) {
        Result<T> result = new Result<T>();
        result.data = object;
        result.code = 1;
        return result;
    }

    public static <T> Result<T> success(String msg, T object) {
        Result<T> result = new Result<T>();
        result.msg = msg;
        result.data = object;
        result.code = 1;
        return result;
    }

    public static <T> Result<T> error(String msg) {
        Result result = new Result();
        result.msg = msg;
        result.code = 0;
        return result;
    }

    public static <T> Result<T> error(String msg,T object) {
        Result result = new Result();
        result.msg = msg;
        result.code = 0;
        result.data = object;
        return result;
    }

}
