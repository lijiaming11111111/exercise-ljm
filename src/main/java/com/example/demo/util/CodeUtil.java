package com.example.demo.util;

import com.example.demo.exception.BaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Random;
@Component
public class CodeUtil {

    static RedisTemplate<String, String> redisTemplate;

    /**
     * 生成指定长度的随机验证码
     *
     * @param length 验证码长度
     * @return 生成的随机验证码
     */
    public static String generateCode(int length) {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    /**
     * 验证验证码是否正确
     *
     * @param key  存储验证码的键
     * @param code 要验证的验证码
     * @return 验证码验证结果，正确返回true，错误抛出异常
     */
    public static Boolean checkCode(String key, String code) {
        String trueCode;
        try {
            trueCode = redisTemplate.opsForValue().get("code:" + key).replaceAll("\"", "");
        } catch (Exception e) {
            trueCode = null;
        }
        if (trueCode == null) {
            throw new BaseException("请先发送验证码");
        }
        if (code.equals(trueCode)) {
            redisTemplate.delete("code:" + key);
            return true;
        }
        throw new BaseException("验证码错误");
    }

    /**
     * 设置RedisTemplate，用于操作Redis
     *
     * @param redisTemplate Redis操作模板
     */
    @Autowired
    public void setUcClient(RedisTemplate<String, String> redisTemplate) {
        CodeUtil.redisTemplate = redisTemplate;
    }


}
