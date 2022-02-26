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

    public boolean moveWindow(String key, SendMessageDto val, Long second) {
        long now = System.currentTimeMillis();
        // 清除超出窗口的数据
        redisTemplate.opsForZSet().removeRangeByScore("window:" + key, 0, now - second * 1000);
        Long count = redisTemplate.opsForZSet().count("window:" + key, now - second * 1000, now);
        if (count >= 10) {
            return false;
        }
        redisTemplate.opsForZSet().add("window:" + key, val, now);
        return true;
    }

}