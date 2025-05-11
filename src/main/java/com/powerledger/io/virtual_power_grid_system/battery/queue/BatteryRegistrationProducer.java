package com.powerledger.io.virtual_power_grid_system.battery.queue;

import com.powerledger.io.virtual_power_grid_system.battery.controller.BatteryController;
import com.powerledger.io.virtual_power_grid_system.battery.dto.BatteryRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BatteryRegistrationProducer {
    private static final Logger LOG = LoggerFactory.getLogger(BatteryRegistrationProducer.class);

    private final RedisTemplate<String, BatteryRequestDto> redisTemplate;
    private static final String BATTERY_QUEUE = "battery:registration:queue";

    public BatteryRegistrationProducer(RedisTemplate<String, BatteryRequestDto> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void enqueueBatteryRegistrations(List<BatteryRequestDto> batteries) {
        LOG.info("Queuing {} batteries for processing", batteries.size());
        batteries.forEach(battery -> 
            redisTemplate.opsForList().rightPush(BATTERY_QUEUE, battery));
    }
}