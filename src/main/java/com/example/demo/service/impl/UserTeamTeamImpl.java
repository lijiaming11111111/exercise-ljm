package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.dto.userTeam.InsertUserTeamDTO;
import com.example.demo.dto.userTeam.PageSelectUserTeamDTO;
import com.example.demo.entity.UserTeam;
import com.example.demo.mapper.UserTeamMapper;
import com.example.demo.result.PageResult;
import com.example.demo.result.Result;
import com.example.demo.service.UserTeamService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.baomidou.mybatisplus.extension.toolkit.Db.saveBatch;

@Service
@RequiredArgsConstructor
public class UserTeamTeamImpl implements UserTeamService {

    private final UserTeamMapper userTeamMapper;

    /**
     * 批量新增用户团队关联关系
     *
     * @param insertUserTeamDTO 包含批量新增用户团队关联信息的数据传输对象
     * @return 新增操作结果提示
     */
    @Override
    public String insertUserTeam(InsertUserTeamDTO insertUserTeamDTO) {
        // 创建集合用于存储用户与团队的关联关系对象
        List<UserTeam> relList = new ArrayList<>();
        // 遍历所有待关联的团队ID
        for (Long teamId : insertUserTeamDTO.getTeamId()) {
            // 遍历所有待关联的用户ID
            for (Long userId : insertUserTeamDTO.getUserId()) {
                // 创建用户与团队的关联关系对象
                UserTeam rel = new UserTeam();
                rel.setTeamId(teamId);
                rel.setUserId(userId);
                relList.add(rel);
            }
        }
        saveBatch(relList);
        return "新增成功";
    }

    /**
     * 批量删除用户团队关联关系
     *
     * @param ids 要删除的用户团队关联关系标识列表
     * @return 批量删除操作结果
     */
    @Override
    public Result deleteUserTeam(List<Long> ids) {
        List<UserTeam> mail=userTeamMapper.selectBatchIds(ids);
        //判断查询的id与表中id是否存在
        if (ids.size()==mail.size()) {
            userTeamMapper.deleteBatchIds(ids);
            return Result.success("删除成功",null);
        }
        return Result.error("删除失败");
    }

    /**
     * 分页查询用户团队关联信息
     *
     * @param pageSelectUserTeamDTO 包含分页查询用户团队关联条件的数据传输对象
     * @return 分页查询到的用户团队关联结果
     */
    @Override
    public PageResult<UserTeam> pageSelectUserTeam(PageSelectUserTeamDTO pageSelectUserTeamDTO) {
        // 创建分页对象，指定页码和每页大小
        Page<UserTeam> page = new Page<>(pageSelectUserTeamDTO.getPage(), pageSelectUserTeamDTO.getPageSize());
        LambdaQueryWrapper<UserTeam> queryWrapper = new LambdaQueryWrapper<>();
        // 构建“团队 ID”模糊查询条件，当团队 ID 列表不为空时生效
        if (pageSelectUserTeamDTO.getTeamId() != null && !pageSelectUserTeamDTO.getTeamId().isEmpty()) {
            queryWrapper.in(UserTeam::getTeamId, pageSelectUserTeamDTO.getTeamId());
        }
        // 构建“用户 ID”模糊查询条件，当用户 ID 列表不为空时生效
        if (pageSelectUserTeamDTO.getUserId() != null && !pageSelectUserTeamDTO.getUserId().isEmpty()) {
            queryWrapper.in(UserTeam::getUserId, pageSelectUserTeamDTO.getUserId());
        }
        Page<UserTeam> result = userTeamMapper.selectPage(page, queryWrapper);
        return new PageResult<>(result.getTotal(), result.getRecords());
    }


}
