package com.stonewu.cmi.controller;

import com.stonewu.cmi.entity.dto.LogoutDto;
import com.stonewu.cmi.entity.dto.UserPasswordDto;
import com.stonewu.cmi.entity.enums.ApiResultType;
import com.stonewu.cmi.entity.result.CommonResult;
import com.stonewu.cmi.entity.result.LoginResult;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequestMapping("/auth")
@RestController
public class AuthController {

    private RedisTemplate redisTemplate;

    public AuthController(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostMapping("/user/register")
    public CommonResult register(@Validated @RequestBody UserPasswordDto userPasswordDto) {
        Object o = redisTemplate.opsForValue().get("user:" + userPasswordDto.getUserName());
        if (o != null) {
            return new CommonResult(ApiResultType.PARAM_ERROR);
        }
        // 模拟入库，暂时存缓存
        redisTemplate.opsForValue().set("user:" + userPasswordDto.getUserName(), userPasswordDto.getPassword());
        return new CommonResult(ApiResultType.SUCCESS);
    }

    @PostMapping("/user/login")
    public CommonResult login(@Validated @RequestBody UserPasswordDto userPasswordDto) {
        Object o = redisTemplate.opsForValue().get("user:" + userPasswordDto.getUserName());
        if (userPasswordDto.getPassword().equals(o)) {
            String sessionId = UUID.randomUUID().toString();
            redisTemplate.opsForValue().set("login:" + userPasswordDto.getUserName(), sessionId);
            return new LoginResult(ApiResultType.SUCCESS, sessionId);
        }
        return new CommonResult(ApiResultType.PARAM_ERROR);
    }

    @PostMapping("/user/logout")
    public CommonResult logout(@Validated @RequestBody LogoutDto logoutDto) {
        Object sessionId = redisTemplate.opsForValue().get("login:" + logoutDto.getUserName());
        if (logoutDto.getSessionId().equals(sessionId)) {
            redisTemplate.delete("login:" + logoutDto.getUserName());
            return new CommonResult(ApiResultType.SUCCESS);
        }
        return new CommonResult(ApiResultType.PARAM_ERROR);
    }
}
