package com.example.git6.service;

import com.example.git6.DTO.InsertMailDTO;
import com.example.git6.DTO.PageSelectMailDTO;
import com.example.git6.entity.Mail;
import com.example.git6.result.PageResult;
import com.example.git6.result.Result;


import java.util.List;

public interface MailService {
    String insertMail(InsertMailDTO insertMailDTO);

    PageResult<Mail> pageSelectMail(PageSelectMailDTO pageSelectMailDTO);

    Result deleteMail(List<Long> ids);
}
