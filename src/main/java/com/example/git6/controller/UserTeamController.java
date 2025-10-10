package com.example.git6.controller;

import com.example.git6.DTO.mail.DeleteMailDTO;
import com.example.git6.DTO.mail.PageSelectMailDTO;
import com.example.git6.DTO.user.DeleteUserTeamDTO;
import com.example.git6.DTO.user.InsertUserTeamDTO;
import com.example.git6.DTO.user.PageSelectUserTeamDTO;
import com.example.git6.entity.Mail;
import com.example.git6.entity.UserTeam;
import com.example.git6.result.PageResult;
import com.example.git6.result.Result;
import com.example.git6.service.UserTeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@CrossOrigin
@Slf4j
@RequiredArgsConstructor
@Tag(name = "用户接口")
@Validated
public class UserTeamController {

    private final UserTeamService userTeamService;

    @PostMapping("/addUserTeam")
    @Operation(summary = "批量新增团队成员")
    public Result<String> addUserTeam(@RequestBody InsertUserTeamDTO insertUserTeamDTO) {
        String userTeam= userTeamService.insertUserTeam(insertUserTeamDTO);
        return Result.success(userTeam,null);
    }

    @PostMapping("/deleteUserTeam")
    @Operation(summary = "批量删除团队成员")
    public Result deleteUserTeam(@RequestBody DeleteUserTeamDTO deleteUserTeamDTO) {
        return userTeamService.deleteUserTeam(deleteUserTeamDTO.getIds());
    }

    @PostMapping("/pageSelectUserTeam")
    @Operation(summary = "分页查询团队成员")
    public Result<PageResult<UserTeam>> pageSelectUserTeam(@RequestBody PageSelectUserTeamDTO pageSelectUserTeamDTO) {
        PageResult<UserTeam> vo =userTeamService.pageSelectUserTeam(pageSelectUserTeamDTO);
        return Result.success("查询成功",vo);
    }
}
