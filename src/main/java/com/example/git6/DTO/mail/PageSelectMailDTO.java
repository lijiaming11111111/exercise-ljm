package com.example.git6.DTO.mail;

import com.example.git6.enums.MailType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PageSelectMailDTO {

    @Schema(description = "发送者邮箱")
    private String sendEmail;

    @Schema(description = "接收者邮箱")
    private String recipientEmail;

    @Schema(description = "邮件内容")
    private String content;

    @Schema(description = "发送状态(1成功;2失败)")
    private MailType status;

    @Schema(description = "页码", defaultValue = "1",required = true)
    private Integer page;

    @Schema(description = "每页显示记录数", defaultValue = "10",required = true)
    private Integer pageSize;
}
