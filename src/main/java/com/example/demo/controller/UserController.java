package com.example.demo.controller;

import com.example.demo.dto.user.*;
import com.example.demo.exception.BaseException;
import com.example.demo.result.PageResult;
import com.example.demo.result.Result;
import com.example.demo.service.UserService;
import com.example.demo.util.CodeUtil;
import com.example.demo.vo.user.PageUserVO;
import com.example.demo.vo.user.UserLoginVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/user")
@CrossOrigin
@Slf4j
@RequiredArgsConstructor
@Validated
@Tag(name = "用户管理")
public class UserController {

    private final UserService userService;

    /**
     * 新增用户
     *
     * @param dto  包含新增用户信息的数据传输对象
     * @return 新增用户操作结果
     * @throws IOException 处理文件时可能抛出的IO异常
     */
    @PostMapping("/addUser")
    @Operation(summary = "新增用户")
    public Result<String>addUser(@Valid @RequestBody AddUserDTO dto) throws IOException {
        return Result.success("新增成功",userService.addUser(dto));
    }

    /**
     * 批量删除用户
     *
     * @param dto 包含要删除用户标识列表的数据传输对象
     * @return 批量删除用户操作结果
     */
    @PostMapping("/deleteUser")
    @Operation(summary = "批量删除用户")
    public Result deleteUser(@RequestBody DeleteUserDTO dto){
         return userService.deleteUser(dto.getIdlist());
    }

    /**
     * 登录
     *
     * @param dto 包含登录信息的数据传输对象
     * @return 登录操作结果，包含用户登录视图对象
     * @throws JsonProcessingException 处理JSON相关操作时可能抛出的异常
     */
    @PostMapping("/login")
    @Operation(summary = "登录")
    public Result<UserLoginVO> login(@Valid @RequestBody UserLoginDTO dto) throws JsonProcessingException {
        return Result.success("登陆成功",userService.login(dto));
    }

    /**
     * 发送验证码
     *
     * @param dto 包含验证码发送相关信息的数据传输对象
     * @return 发送验证码操作结果
     */
    @PostMapping("/sendVerificationCode")
    @Operation(summary = "发送验证码")
    public Result sendVerificationCode(@RequestBody SendVerificationCodeDTO dto){
        return userService.sendVerificationCode(dto);
    }

    /**
     * 验证码验证
     *
     * @param dto 包含验证码验证相关信息的数据传输对象
     * @return 验证码验证操作结果
     */
    @PostMapping("/verificationCodeValidation")
    @Operation(summary = "验证码验证")
    public Result verificationCodeValidation(@RequestBody VerificationCodeValidationDTO dto){
        if (CodeUtil.checkCode(dto.getMail(), dto.getVerificationCode())) {
            return Result.success("验证成功", true);
        }
        throw new BaseException("验证码错误");
    }

    /**
     * 忘记密码
     *
     * @param dto 包含忘记密码相关信息的数据传输对象
     * @return 忘记密码（重置密码）操作结果
     */
    @PostMapping("/forgetPassword")
    @Operation(summary = "忘记密码")
    public Result forgetPassword(@RequestBody ForgetPasswordDTO dto){
        userService.forgetPassword(dto);
        return Result.success("修改成功",null);
    }

    /**
     * 修改密码
     *
     * @param dto 包含密码修改信息的数据传输对象
     * @return 修改密码操作结果
     */
    @PostMapping("/updatePassword")
    @Operation(summary = "修改密码")
    public Result updatePassword(@RequestBody UpdatePasswordDTO dto){
        return userService.updatePassword(dto);
    }

    /**
     * 分页查询用户
     *
     * @param dto 包含分页查询用户条件的数据传输对象
     * @return 分页查询到的用户结果
     */
    @PostMapping("/pageUser")
    @Operation(summary = "分页查询用户")
    public Result<PageResult<PageUserVO>>pageUser(@RequestBody PageUserDTO dto){
        PageResult<PageUserVO>pageResult=userService.pageUser(dto);
        return Result.success("查询成功",pageResult);
    }

    /**
     * 修改用户
     *
     * @param updateUserDTO 包含用户修改信息的数据传输对象
     * @param face          用户头像文件（可选）
     * @return 修改用户操作结果
     * @throws IOException 处理文件时可能抛出的IO异常
     */
    @PostMapping("/updateUser")
    @Operation(summary = "修改用户")
    public Result updateUser(@Valid @RequestPart("dto")  UpdateUserDTO updateUserDTO,
                             @RequestPart(value = "face", required = false) MultipartFile face) throws IOException {
        return userService.updateUser(updateUserDTO,face );
    }

    @PostMapping("/updateUser")
    @Operation(summary = "测试")
    public Result updateUser(){
        return null;
    }
}
