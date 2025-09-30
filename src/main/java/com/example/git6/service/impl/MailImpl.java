package com.example.git6.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.git6.DTO.InsertMailDTO;
import com.example.git6.DTO.PageSelectMailDTO;
import com.example.git6.entity.Mail;
import com.example.git6.mapper.MailMapper;
import com.example.git6.result.PageResult;
import com.example.git6.result.Result;
import com.example.git6.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.git6.enums.MailType.FAIL;
import static com.example.git6.enums.MailType.SUCCEED;

@Service
@RequiredArgsConstructor
public class MailImpl implements MailService {
    @Autowired
    private JavaMailSender mailSender;

    private final MailMapper mailMapper;

    @org.springframework.beans.factory.annotation.Value("${spring.mail.username}")
    private String senderMail;


    @Override
    public String insertMail(InsertMailDTO insertMailDTO) {
        Mail mail = new Mail();
        mail.setId(IdWorker.getId());
        mail.setSendEmail(senderMail);
        mail.setSendDate(LocalDateTime.now());
        mail.setContent(insertMailDTO.getContent());
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            //发送邮箱
            message.setFrom(senderMail);                                        // 发件人（需与配置中的 username 一致）
            message.setTo(insertMailDTO.getRecipientEmail());                   // 收件人
            message.setText(insertMailDTO.getContent());
            mailSender.send(message);
            //修改状态
            mail.setStatus(SUCCEED);
            mail.setRecipientEmail(insertMailDTO.getRecipientEmail());
            mailMapper.insert(mail);
            return "邮件发送成功";
        }catch (Exception e){
            //修改状态
            mail.setStatus(FAIL);
            mail.setRecipientEmail(insertMailDTO.getRecipientEmail());
            mailMapper.insert(mail);
            return "邮件发送失败";
        }

    }

    @Override
    public PageResult<Mail> pageSelectMail(PageSelectMailDTO pageSelectMailDTO) {
        Page<Mail> page = new Page<>(pageSelectMailDTO.getPage(), pageSelectMailDTO.getPageSize());
        LambdaQueryWrapper<Mail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(pageSelectMailDTO.getSendEmail()),
                Mail::getSendEmail, pageSelectMailDTO.getSendEmail());
        queryWrapper.like(StringUtils.isNotBlank(pageSelectMailDTO.getRecipientEmail()),
                Mail::getRecipientEmail, pageSelectMailDTO.getRecipientEmail());
        queryWrapper.like(StringUtils.isNotBlank(pageSelectMailDTO.getContent()),
                Mail::getContent, pageSelectMailDTO.getContent());
        queryWrapper.eq(pageSelectMailDTO.getStatus()!=null, Mail::getStatus, pageSelectMailDTO.getStatus());
        Page<Mail>result=mailMapper.selectPage(page,queryWrapper);
        return new PageResult<>(result.getTotal(),result.getRecords());
    }

    @Override
    public Result deleteMail(List<Long> ids) {
        List<Mail> mail=mailMapper.selectBatchIds(ids);
        //判断查询的id与表中id是否存在
        if (ids.size()==mail.size()) {
            mailMapper.deleteBatchIds(ids);
            return Result.success("删除成功",null);
        }
        return Result.error("删除失败");
    }
}
