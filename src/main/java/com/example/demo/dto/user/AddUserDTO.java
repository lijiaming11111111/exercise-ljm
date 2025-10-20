package com.example.demo.dto.user;


import com.example.demo.enums.SexEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

@Data
public class AddUserDTO {
    @Schema(description = "用户名",required = true)
    private String userName;

    @Schema(description = "加密后的密码",required = true)
    @Size(min = 6, max = 32, message = "请输入8~32位密码")
    private String password;

    @Schema(description = "邮箱",required = true)
    private String mail;

    @Schema(description = "手机号",required = true)
    @Size(max = 11, message = "联系电话长度超过限制")
    private String mobile;

    @Schema(description = "地址",required = true)
    private String address;

    @Schema(description = "性别枚举对象WOMAN女，MAN男",required = true)
    private SexEnum sexEnum;

    @Schema(description = "出生年月",required = true)
    private Date birthday;

}
