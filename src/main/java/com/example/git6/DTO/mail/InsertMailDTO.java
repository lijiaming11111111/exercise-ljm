package com.example.git6.DTO.mail;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class InsertMailDTO {
    @Schema(description = "接收者邮箱",required = true)
    private String recipientEmail;

    @Schema(description = "邮件内容",required = true)
    private String content;
}
