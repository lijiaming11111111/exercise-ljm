package com.example.git6.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.git6.DTO.user.PageSelectUserTeamDTO;
import com.example.git6.entity.UserTeam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface UserTeamMapper extends BaseMapper<UserTeam> {
    IPage<UserTeam> selectUserTeamPage(PageSelectUserTeamDTO queryDTO);
}
