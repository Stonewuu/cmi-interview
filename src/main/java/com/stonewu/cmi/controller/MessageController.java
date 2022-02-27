package com.stonewu.cmi.controller;

import com.stonewu.cmi.entity.dto.MessageAuthDto;
import com.stonewu.cmi.entity.dto.MessageDto;
import com.stonewu.cmi.entity.dto.SendMessageDto;
import com.stonewu.cmi.entity.enums.ApiResultType;
import com.stonewu.cmi.entity.result.CommonResult;
import com.stonewu.cmi.utils.MessageSender;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RequestMapping("")
@RestController
public class MessageController {

    private RedisTemplate redisTemplate;

    private MessageSender messageSender;

    public MessageController(RedisTemplate redisTemplate, MessageSender messageSender) {
        this.redisTemplate = redisTemplate;
        this.messageSender = messageSender;
    }

    @PostMapping("/directmessage")
    public CommonResult directmessage(@Validated MessageAuthDto messageAuthDto, @Validated @RequestBody MessageDto messageDto) {
        Object sessionId = redisTemplate.opsForValue().get("login:" + messageAuthDto.getUserName());
        if (!messageAuthDto.getSessionId().equals(sessionId)) {
            return new CommonResult(ApiResultType.NO_AUTH_ERROR);
        }
        // 发短信
        SendMessageDto sendMessageDto = new SendMessageDto();
        sendMessageDto.setQos(messageAuthDto.getQos());
        sendMessageDto.setAcceptorTel(messageAuthDto.getTels());
        sendMessageDto.setTemplateParam(messageDto);
        sendMessageDto.setTimestamp(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()));
        // 发送短信
        Future<Boolean> booleanFuture = messageSender.sendMessage(sendMessageDto);
        try {
            Boolean send = booleanFuture.get();
            if (!send) {
                return new CommonResult(ApiResultType.SERVER_ERROR);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return new CommonResult(ApiResultType.SUCCESS);
    }
}
