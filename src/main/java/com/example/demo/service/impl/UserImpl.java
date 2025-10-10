package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.bo.UserLoginVerifyData;
import com.example.demo.dto.user.*;

import com.example.demo.entity.User;
import com.example.demo.enums.StatusEnum;
import com.example.demo.exception.BaseException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.result.PageResult;
import com.example.demo.result.Result;
import com.example.demo.service.UserService;
import com.example.demo.util.JwtUtil;
import com.example.demo.util.SaltUtil;
import com.example.demo.vo.UserLoginVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserImpl extends ServiceImpl<UserMapper, User>implements UserService {
    private final UserMapper userMapper;

    @Value("${jwt.secretKey}")
    private String jwtSecretKey;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @Autowired
    private JavaMailSenderImpl javaMailSender;

    @Value("${spring.mail.username}")
    private String sendMailer;


    private String verificationCode;

    private String mail;

    private Date startTime;

    private Date endTime;

    private Boolean authCode =false;

    /**
     * 新增用户
     *
     * @param  addUserDTO
     * @return
     */
    @Override
    public String addUser(AddUserDTO addUserDTO) {
        //创建用户
        User user = new User();
        BeanUtils.copyProperties(addUserDTO,user);
        user.setId(IdWorker.getId());
        user.setStatusEnum(StatusEnum.START);

        QueryWrapper<User>queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("mail",addUserDTO.getMail());
        Long count=userMapper.selectCount(queryWrapper);
        if (count!=0){
            throw new BaseException("邮箱已存在");
        }

        //生成盐值和加密密码
        String salt= SaltUtil.generateSalt(16);
        user.setSalt(salt);
        String password=addUserDTO.getPassword()+salt;
        user.setPassword(DigestUtils.md5DigestAsHex(password.getBytes()));
        if (userMapper.insert(user)!=1){
            throw new BaseException("新增失败");
        }
        return user.getId().toString();
    }

    /**
     * 批量删除用户
     *
     * @param  ids
     * @return
     */
    @Override
    public Result deleteUser(List<Long> ids) {
        List<User>userList=userMapper.selectBatchIds(ids);

        //判断删除的邮件历史记录id是否存在
        if (ids.size()== userList.size()){
            userMapper.deleteBatchIds(ids);
            return Result.success("删除成功");
        }else {
            return Result.error("删除失败，邮件不存在");
        }
    }

    /**
     * 登录
     *
     * @param  dto
     * @return
     */
    @Override
    public UserLoginVO login(UserLoginDTO dto) throws JsonProcessingException {
        QueryWrapper<User>queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("mail",dto.getMail());
        User user=userMapper.selectOne(queryWrapper);
        if (user==null){
            throw new BaseException("用户不存在");
        }
        UserLoginVerifyData data=new UserLoginVerifyData();
        BeanUtils.copyProperties(user,data);
        String password=dto.getPassword()+data.getSalt();
        password=DigestUtils.md5DigestAsHex(password.getBytes());

        if (!password.equals(user.getPassword())) {
            //密码错误
            throw new BaseException("密码错误");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", data.getId());
        //生成token
        String token = JwtUtil.createJWT(
                jwtSecretKey,
                jwtExpiration * 3600 * 1000,
                claims);
        return UserLoginVO
                .builder()
                .id(data.getId())
                .userName(data.getUserName())
                .token(token).build();
    }

    @Override
    public Result sendVerificationCode(SendVerificationCodeDTO dto) {
        SimpleMailMessage message = new SimpleMailMessage();
        Random random = new Random();
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            int r = random.nextInt(10);
            code.append(r);
        }
        verificationCode= String.valueOf(code);
        String text = "您的验证码为：" + code + ",请勿泄露给他人。";
        message.setFrom(sendMailer);
        message.setTo(dto.getMail());
        message.setText(text);
        message.setSentDate(new Date());
        message.setSubject("登录验证码");
        mail=dto.getMail();

        startTime=message.getSentDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime(startTime);
        cal.add(Calendar.MINUTE,5);
        endTime=cal.getTime();

        //判断是否发送失败
        try {
            javaMailSender.send(message);
            return Result.success("发送成功",null);

        }catch (Exception e){
            return Result.error("发送失败");
        }

    }

    /**
     * 验证码验证
     *
     * @param  dto
     * @return
     */
    @Override
    public Result verificationCodeValidation(VerificationCodeValidationDTO dto) {
        if (Objects.equals(dto.getMail(), mail) &&dto.getVerificationCode().equals(verificationCode)){
            if (new Date().after(endTime)){
                mail=null;
                verificationCode=null;
                //邮箱验证码发送时间
                startTime=null;
                //邮箱验证码有效期结束时间
                endTime=null;
                return Result.error("验证码失效");
            }else {
                authCode =true;
                return Result.success("验证成功",null);
            }
        }else {
            authCode =false;
            return Result.error("无效验证码");
        }
    }

    /**
     * 忘记密码
     *
     * @param  dto
     * @return
     */
    @Override
    public Result forgetPassword(ForgetPasswordDTO dto) {
        // 验证标识判断
        if (authCode ==true){
            QueryWrapper<User>queryWrapper=new QueryWrapper<>();
            queryWrapper.eq("mail",dto.getMail());
            User oldUser=userMapper.selectOne(queryWrapper);

            //判断用户是否存在，不存在则返回错误信息
            if (oldUser==null){
                return Result.error("用户不存在");
            }

            //构建旧密码校验值：输入的旧密码明文 + 数据库中存储的盐值
            String oldPassword=dto.getOldPassword()+oldUser.getSalt();
            oldPassword=DigestUtils.md5DigestAsHex(oldPassword.getBytes());

            //校验旧密码是否正确
            if (oldPassword.equals(oldUser.getPassword())){
                String salt = SaltUtil.generateSalt(16);
                oldUser.setSalt(salt);
                String newPassword = dto.getNewPassword() + salt;
                oldUser.setPassword(DigestUtils.md5DigestAsHex(newPassword.getBytes()));
                User newUser = new User();
                BeanUtils.copyProperties(oldUser,newUser);
                userMapper.updateById(newUser);
                return Result.success("修改成功 ");
            }else {
                return Result.error("密码错误");
            }

        }else {
            return Result.error("没有通过验证码验证，不能修改密码");
        }
    }

    /**
     * 修改密码
     *
     * @param  dto
     * @return
     */
    @Override
    public Result updatePassword(UpdatePasswordDTO dto) {
        QueryWrapper<User>queryWrapper=new QueryWrapper<>();
        // 设置查询条件：根据邮箱查询用户
        queryWrapper.eq("mail",dto.getMail());
        //  根据邮箱查询用户信息
        User oldUser=userMapper.selectOne(queryWrapper);
        //判断用户是否存在，不存在则返回错误信息
        if (oldUser==null){
            return Result.error("用户不存在");
        }
        //生成16位随机盐值，用于密码加密
        String salt = SaltUtil.generateSalt(16);
        //将盐值设置到用户对象中
        oldUser.setSalt(salt);
        //构建新密码：新密码明文 + 盐值
        String newPassword = dto.getNewPassword() + salt;
        //对新密码进行MD5加密处理，并设置到用户对象中
        oldUser.setPassword(DigestUtils.md5DigestAsHex(newPassword.getBytes()));
        User newUser = new User();
        BeanUtils.copyProperties(oldUser,newUser);
        userMapper.updateById(newUser);
        return Result.success("修改成功 ");
    }

    /**
     * 分页查询邮件历史记录
     *
     * @param  dto
     * @return
     */
    @Override
    public PageResult<User> pageUser(PageUserDTO dto) {
        //创建分页对象
        Page<User>page=new Page<>(dto.getPage(),dto.getPageSize());
        //创建LambdaQueryWrapper条件构造器
        LambdaQueryWrapper<User>queryWrapper=new LambdaQueryWrapper<>();
        //构建模糊用户名
        queryWrapper.like(StringUtils.isNotBlank(dto.getUserName()),
                User::getUserName,dto.getUserName());
        //构建模糊邮箱
        queryWrapper.like(StringUtils.isNotBlank(dto.getMail()),
                User::getMail,dto.getMail());
        //构建模糊手机号
        queryWrapper.like(StringUtils.isNotBlank(dto.getMobile()),
                User::getMobile,dto.getMobile());
        //构建模糊地址
        queryWrapper.like(StringUtils.isNotBlank(dto.getAddress()),
                User::getAddress,dto.getAddress());
        //构建精确查询性别
        queryWrapper.eq(dto.getSexEnum()!=null, User::getSexEnum,dto.getSexEnum());
        //构建精确查询状态
        queryWrapper.eq(dto.getStatusEnum()!=null, User::getStatusEnum,dto.getStatusEnum());
        //调用mapper层方法执行分页查询，获取查询结果
        Page<User>result=userMapper.selectPage(page,queryWrapper);

        return new PageResult<>(result.getTotal(),result.getRecords());
    }

    /**
     * 修改用户
     *
     * @param  dto
     * @return
     */
    @Override
    public Result updateUser(UpdateUserDTO dto) {
        QueryWrapper<User>queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("id",dto.getId());
        User oldUser=userMapper.selectOne(queryWrapper);
        if (oldUser==null){
            return Result.error("用户不存在");
        }
        User user=new User();
        BeanUtils.copyProperties(oldUser,user);
        BeanUtils.copyProperties(dto,user);
        QueryWrapper<User>oldQueryWrapper=new QueryWrapper<>();
        oldQueryWrapper.eq("id",dto.getId());
        userMapper.update(user,oldQueryWrapper);
        return Result.success("修改成功",null);
    }

    /**
     * 每十秒执行一次，验证邮箱验证码是否过期
     *
     *
     *
     */
    @Scheduled(cron = "*/10 * * * * *")
    public void timekeeping(){
        if (new Date().after(endTime)){
            mail=null;
            verificationCode=null;
            startTime=null;
            endTime=null;
            authCode =false;
        }
    }
}
