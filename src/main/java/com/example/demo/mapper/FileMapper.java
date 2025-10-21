package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.File;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FileMapper extends BaseMapper<File> {
    /**
     * @param objectName
     * 根据源文件名查询
     */
    @Select("select id from file where object_name=#{objectName} ")
    Long selectFileId(String objectName);

    /**
     * 根据查询文件
     */
    @Select("select id from file")
    List<Long> selectAllFileId();
}
