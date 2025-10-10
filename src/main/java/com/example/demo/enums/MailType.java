package com.example.demo.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "发送状态(1成功;2失败)")
public enum MailType {

    /**
     * 发送状态-成功
     */
    SUCCEED(1, "成功"),

    /**
     * 发送状态-失败
     */
    FAIL(2, "失败");

    @JsonValue
    @EnumValue
    private final Integer code;

    private final String desc;
}
