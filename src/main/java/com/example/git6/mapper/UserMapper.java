package com.example.git6.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.git6.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
