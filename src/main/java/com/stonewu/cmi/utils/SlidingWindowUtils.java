package com.stonewu.cmi.utils;

import com.stonewu.cmi.entity.dto.SendMessageDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class SlidingWindowUtils {
    private RedisTemplate redisTemplate;

    public SlidingWindowUtils(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public synchronized Long moveWindow(SendMessageDto val, Long second) {
        long now = System.currentTimeMillis();
        // 清除超出窗口的数据
        if(val.getQos() == 3){
            redisTemplate.opsForZSet().removeRangeByScore("window", 0, now - second * 1000);
        }
        Long count = redisTemplate.opsForZSet().count("window", now - second * 1000, now);
        if (count >= 10) {
            return count;
        }
        redisTemplate.opsForZSet().add("window", val, now);
        return count;
    }

}
