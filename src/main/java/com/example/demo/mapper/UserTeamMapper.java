package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo.dto.userTeam.PageSelectUserTeamDTO;
import com.example.demo.entity.UserTeam;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserTeamMapper extends BaseMapper<UserTeam> {

}
