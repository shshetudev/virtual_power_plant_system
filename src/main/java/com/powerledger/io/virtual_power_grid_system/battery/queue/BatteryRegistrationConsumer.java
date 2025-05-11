package com.powerledger.io.virtual_power_grid_system.battery.queue;

import com.powerledger.io.virtual_power_grid_system.battery.dto.BatteryRequestDto;
import com.powerledger.io.virtual_power_grid_system.battery.service.BatteryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class BatteryRegistrationConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(BatteryRegistrationConsumer.class);
    private static final long BATCH_PROCESSING_TIME_IN_MILLISECONDS = 1000;
    private static final int BATCH_PROCESSING_LIMIT = 100;
    private static final String BATTERY_QUEUE = "battery:registration:queue";

    private final RedisTemplate<String, BatteryRequestDto> redisTemplate;
    private final BatteryService batteryService;

    public BatteryRegistrationConsumer(RedisTemplate<String, BatteryRequestDto> redisTemplate, BatteryService batteryService) {
        this.redisTemplate = redisTemplate;
        this.batteryService = batteryService;
    }

    @Scheduled(fixedRate = BATCH_PROCESSING_TIME_IN_MILLISECONDS)
    public void processBatteryRegistrations() {
        List<BatteryRequestDto> batchToSave = Stream.generate(() ->
                        redisTemplate.opsForList().leftPop(BATTERY_QUEUE))
                .limit(BATCH_PROCESSING_LIMIT)
                .takeWhile(Objects::nonNull)
                .toList();

        if (!batchToSave.isEmpty()) {
            LOG.info("Processing batch of {} batteries", batchToSave.size());
            batteryService.saveAll(batchToSave);
        }
    }
}