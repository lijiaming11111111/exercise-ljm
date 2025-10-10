package com.example.git6.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@TableName("user")
public class User {
    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名称")
    private String name;

    @Schema(description = "用户邮箱")
    private String email;
}
