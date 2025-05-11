package com.powerledger.io.virtual_power_grid_system.config;

import com.powerledger.io.virtual_power_grid_system.battery.dto.BatteryRequestDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, BatteryRequestDto> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, BatteryRequestDto> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(BatteryRequestDto.class));
        return template;
    }
}