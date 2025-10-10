package com.example.demo.dto.mail;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class DeleteMailDTO {
    @Schema(description = "邮件ID")
    private List<Long> ids;
}
