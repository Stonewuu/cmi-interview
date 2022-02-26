package com.stonewu.cmi.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.jayway.jsonpath.JsonPath;
import com.stonewu.cmi.entity.dto.SendMessageDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Component
public class MessageSender {
    private ObjectMapper objectMapper = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

    private ExecutorService executor;

    private SlidingWindowUtils slidingWindowCounter;

    private RedisTemplate redisTemplate;

    public MessageSender(ExecutorService executor, SlidingWindowUtils slidingWindowCounter, RedisTemplate redisTemplate) {
        this.executor = executor;
        this.slidingWindowCounter = slidingWindowCounter;
        this.redisTemplate = redisTemplate;
    }

    public Future<Boolean> sendMessage(SendMessageDto sendMessageDto) {
        return executor.submit(() -> {
            boolean flag = false;
            long time = System.currentTimeMillis();
            Integer qos = sendMessageDto.getQos();
            while (true) {
                boolean enter = slidingWindowCounter.moveWindow("key", sendMessageDto, 1L);
                if (!enter) {
                    //窗口时间内请求已满
                    if (qos == 1 && System.currentTimeMillis() - time < 4000) {
                        // 高优先级消息可延迟发送
                        Thread.sleep(100);
                        continue;
                    }
                    if (qos == 2 && System.currentTimeMillis() - time < 1000) {
                        Thread.sleep(200);
                        continue;
                    }
                } else {
                    Object val = redisTemplate.opsForValue().get("phone:" + sendMessageDto.getAcceptorTel());
                    if (val == null) {
                        val = redisTemplate.opsForValue().getAndSet("phone:" + sendMessageDto.getAcceptorTel(), 1);
                        redisTemplate.expire("phone:" + sendMessageDto.getAcceptorTel(), 1100, TimeUnit.MILLISECONDS);
                        if (val == null) {
                            String json = objectMapper.writeValueAsString(sendMessageDto);
                            String result = HttpClient.doPost("http://127.0.0.1:8081/v2/emp/templateSms/sendSms", json);
                            String code = JsonPath.read(result, "$.res_code");
                            if (Integer.valueOf(code) == 0) {
                                flag = true;
                            } else {
                                System.err.println("发送失败：" + result);
                            }
                        }
                    } else {
                    }
                }
                return flag;
            }
        });
    }


}
