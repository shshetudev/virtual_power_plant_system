package com.powerledger.io.virtual_power_grid_system.battery.service;

import com.powerledger.io.virtual_power_grid_system.battery.dto.BatteryRangeResponseDto;
import com.powerledger.io.virtual_power_grid_system.battery.dto.BatteryRequestDto;

import java.util.List;

public interface BatteryService {
    void saveAll(List<BatteryRequestDto> batteryRequestDtos);
    BatteryRangeResponseDto getBatteriesByPostcodeRange(Integer from, Integer to);
    BatteryRangeResponseDto getBatteriesByPostcodeRange(Integer from, Integer to,
                                                        Long minWattCapacity,
                                                        Long maxWattCapacity);
}
