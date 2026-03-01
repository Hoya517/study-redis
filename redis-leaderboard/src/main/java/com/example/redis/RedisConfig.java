package com.example.redis;

import com.example.redis.domain.ItemDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, ItemDto> rankTemplate (
            RedisConnectionFactory redisConnectionFactory
    ) {
        RedisTemplate<String, ItemDto> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        // 주어진 데이터를 어떻게 직렬화 할것인지
        template.setKeySerializer(RedisSerializer.string());
        template.setValueSerializer(RedisSerializer.json());
        return template;
    }
}
