package com.example.demo.controller;

import com.example.demo.dto.mail.DeleteMailDTO;
import com.example.demo.dto.mail.InsertMailDTO;
import com.example.demo.dto.mail.PageSelectMailDTO;
import com.example.demo.entity.Mail;
import com.example.demo.result.PageResult;
import com.example.demo.result.Result;
import com.example.demo.service.MailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email")
@CrossOrigin
@Slf4j
@RequiredArgsConstructor
@Tag(name = "邮件接口")
@Validated
public class MailController {

    private final MailService mailService;

    /**
     * mail 新增的邮件信息
     * @param insertMailDTO 包含新增邮件信息的数据传输对象
     * @return 新增邮件操作结果
     */
    @PostMapping("/addMail")
    @Operation(summary = "新增邮件")
    public Result<String> addMail(@RequestBody InsertMailDTO insertMailDTO) {
        String mail=mailService.insertMail(insertMailDTO);
        return Result.success(mail,null);
    }

    /**
     * 分页查询邮件相关信息
     * @param pageSelectMailDTO 包含分页查询条件的数据传输对象
     * @return 分页查询到的邮件结果
     */
    @PostMapping("/pageSelectMail")
    @Operation(summary = "分页查询邮件")
    public Result<PageResult<Mail>> pageSelectMail(@RequestBody PageSelectMailDTO pageSelectMailDTO) {
        PageResult<Mail> vo =mailService.pageSelectMail(pageSelectMailDTO);
        return Result.success("查询成功",vo);
    }

    /**
     * 批量删除邮件
     * @param deleteMailDTO 包含要删除邮件标识的数据传输对象
     * @return 批量删除邮件操作结果
     */
    @PostMapping("/deleteMail")
    @Operation(summary = "批量删除邮件")
    public Result deleteMail(@RequestBody DeleteMailDTO deleteMailDTO) {
        return mailService.deleteMail(deleteMailDTO.getIds());
    }
}
