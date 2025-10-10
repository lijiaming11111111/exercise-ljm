package com.example.git6.service;

import com.example.git6.DTO.mail.InsertMailDTO;
import com.example.git6.DTO.mail.PageSelectMailDTO;
import com.example.git6.entity.Mail;
import com.example.git6.result.PageResult;
import com.example.git6.result.Result;


import java.util.List;

public interface MailService {
    /**
     * 新增邮件信息
     * @param insertMailDTO 邮件信息DTO对象
     * @return 操作结果信息
     */
    String insertMail(InsertMailDTO insertMailDTO);

    /**
     * 分页查询邮件信息
     * @param pageSelectMailDTO 分页查询条件DTO对象
     * @return 分页查询结果，包含邮件列表及分页信息
     */
    PageResult<Mail> pageSelectMail(PageSelectMailDTO pageSelectMailDTO);

    /**
     * 批量删除邮件
     * @param ids 待删除邮件的ID列表
     * @return 操作结果对象
     */
    Result deleteMail(List<Long> ids);
}
