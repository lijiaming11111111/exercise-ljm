package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.demo.enums.MailType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@TableName("mail")
public class Mail {
    @Schema(description = "邮件ID")
    private Long id;

    @Schema(description = "发送者邮箱")
    private String sendEmail;

    @Schema(description = "发送时间")
    private LocalDateTime sendDate;

    @Schema(description = "接收者邮箱")
    private String recipientEmail;

    @Schema(description = "发送状态(1成功;2失败)")
    private MailType status;

    @Schema(description = "邮件内容")
    private String content;
}