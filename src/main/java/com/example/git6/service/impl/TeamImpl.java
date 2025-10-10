package com.example.git6.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.git6.DTO.team.AddTeamMailDTO;
import com.example.git6.DTO.team.InsertTeamDTO;
import com.example.git6.DTO.team.PageSelectTeamDTO;
import com.example.git6.entity.Mail;
import com.example.git6.entity.Team;
import com.example.git6.entity.UserTeam;
import com.example.git6.entity.User;
import com.example.git6.mapper.MailMapper;
import com.example.git6.mapper.TeamMapper;
import com.example.git6.mapper.UserMapper;
import com.example.git6.mapper.UserTeamMapper;
import com.example.git6.result.PageResult;
import com.example.git6.result.Result;
import com.example.git6.service.TeamService;
import lombok.RequiredArgsConstructor;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.git6.enums.MailType.FAIL;
import static com.example.git6.enums.MailType.SUCCEED;

@Service
@RequiredArgsConstructor
public class TeamImpl implements TeamService {

    @Autowired
    private JavaMailSender mailSender;

    private final MailMapper mailMapper;

    private final TeamMapper teamMapper;

    private final UserMapper userMapper;

    private final UserTeamMapper userTeamMapper;

    @Override
    public String insertTeam(InsertTeamDTO insertTeamDTO) {
        //创建对象
        Team team = new Team();
        team.setId(IdWorker.getId());
        team.setTeamName(insertTeamDTO.getTeamName());
        team.setTeamIntroduction(insertTeamDTO.getTeamIntroduction());
        team.setContactInformation(insertTeamDTO.getContactInformation());
        team.setAddress(insertTeamDTO.getAddress());
        teamMapper.insert(team);
        return "新增成功";
    }

    @Override
    public String insertTeamMail(AddTeamMailDTO addTeamMailDTO) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 使用 in 方法，查询团队ID
        List<UserTeam> userTeams=userTeamMapper.selectList(new QueryWrapper<UserTeam>().in("team_id", addTeamMailDTO.getId()));
        // 根据团队ID(team_id)，提取 用户ID(user_id) 并返回
        List<Long> userId=userTeams.stream().map(UserTeam::getUserId).collect(Collectors.toList());
        List<User> userList = userMapper.selectList(queryWrapper.in("id", userId));
        // 根据用户ID(user_id),提取 用户ID所对应的邮箱(email)
        List<String> emails= userList.stream().map(User::getEmail).collect(Collectors.toList());

        //批量发送邮件
        for (String email : emails) {
            Mail mail = new Mail();
            mail.setId(IdWorker.getId());
            mail.setSendEmail("196254331@qq.com");
            mail.setSendDate(LocalDateTime.now());
            mail.setContent(addTeamMailDTO.getContent());

            //尝试发送邮件
            try {
                //发送邮件成功，生成对应的数据库内容
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom("196254331@qq.com");                  // 发件人（需与配置中的 username 一致）
                message.setTo(email);                                 // 收件人
                message.setText(addTeamMailDTO.getContent());
                mailSender.send(message);
                //修改状态
                mail.setStatus(SUCCEED);
                mail.setRecipientEmail(email);
                mailMapper.insert(mail);
            }catch (Exception e){
                //异常处理
                mail.setStatus(FAIL);
                mail.setRecipientEmail(email);
                mailMapper.insert(mail);
            }
        }
        return "批量新增成功";
    }

    @Override
    public PageResult<Team> pageSelectTeam(PageSelectTeamDTO pageSelectTeamDTO) {
        //创建分页对象，指定页码和每页大小
        Page<Team> page = new Page<>(pageSelectTeamDTO.getPage(), pageSelectTeamDTO.getPageSize());
        //创建 Lambda 形式的查询条件构造器
        LambdaQueryWrapper<Team> queryWrapper = new LambdaQueryWrapper<>();
        //模糊查询团队名称
        queryWrapper.like(StringUtils.isNotBlank(pageSelectTeamDTO.getTeamName()),
                Team::getTeamName, pageSelectTeamDTO.getTeamName());
        //模糊查询团队介绍
        queryWrapper.like(StringUtils.isNotBlank(pageSelectTeamDTO.getTeamIntroduction()),
                Team::getTeamIntroduction, pageSelectTeamDTO.getTeamIntroduction());
        //模糊查询联系方式
        queryWrapper.like(StringUtils.isNotBlank(pageSelectTeamDTO.getContactInformation()),
                Team::getContactInformation, pageSelectTeamDTO.getContactInformation());
        //模糊查询地址
        queryWrapper.like(StringUtils.isNotBlank(pageSelectTeamDTO.getAddress()),
                Team::getAddress, pageSelectTeamDTO.getAddress());
        Page<Team>result=teamMapper.selectPage(page,queryWrapper);
        return new PageResult<>(result.getTotal(),result.getRecords());
    }

    @Override
    public Result deleteTeam(List<Long> ids) {
        List<Team> team=teamMapper.selectBatchIds(ids);
        //判断查询的id与表中id是否存在
        if (ids.size()==team.size()) {
            teamMapper.deleteBatchIds(ids);
            return Result.success("删除成功",null);
        }
        return Result.error("删除失败");
    }

}
