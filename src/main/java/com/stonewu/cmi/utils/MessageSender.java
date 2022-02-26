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
                Long count = slidingWindowCounter.moveWindow( sendMessageDto, 1L);
                if (count >= 10) {
                    //窗口时间内请求已满
                    if (qos == 1 && System.currentTimeMillis() - time < 4000) {
                        // 高优先级消息可延迟发送
                        Thread.sleep(100);
                        continue;
                    }else if (qos == 2 && System.currentTimeMillis() - time < 2000) {
                        Thread.sleep(200);
                        continue;
                    }
                    System.err.println("发送失败：窗口拦截，QOS：" + qos);
                } else {
                    if(count >= 8 && qos == 3 && System.currentTimeMillis() - time < 4000){
                        redisTemplate.opsForZSet().remove("window", sendMessageDto);
                        // 4秒内有资源留给高优先级处理
                        Thread.sleep(1000);
                        continue;
                    }else if (count >= 8 && qos == 2 && System.currentTimeMillis() - time < 2000) {
                        Thread.sleep(1000);
                        continue;
                    }
                    Object val = redisTemplate.opsForValue().get("phone:" + sendMessageDto.getAcceptorTel());
                    if (val == null) {
                        val = redisTemplate.opsForValue().getAndSet("phone:" + sendMessageDto.getAcceptorTel(), 1);
                        redisTemplate.expire("phone:" + sendMessageDto.getAcceptorTel(), 1, TimeUnit.SECONDS);
                        if (val == null) {
                            String json = objectMapper.writeValueAsString(sendMessageDto);
                            String result = HttpClient.doPost("http://127.0.0.1:8081/v2/emp/templateSms/sendSms", json);
                            String code = JsonPath.read(result, "$.res_code");
                            if (Integer.valueOf(code) == 0) {
                                flag = true;
                            } else {
                                System.err.println("发送失败：" + result+"，QOS：" + qos);
                            }
                        } else {
                            System.err.println("发送失败：号码限制内部拦截，QOS：" + qos);
                        }
                    } else {
                        System.err.println("发送失败：号码限制外部拦截，QOS：" + qos);
                    }
                }
                return flag;
            }
        });
    }


}
