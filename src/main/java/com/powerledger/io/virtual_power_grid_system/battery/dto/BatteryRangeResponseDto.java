package com.powerledger.io.virtual_power_grid_system.battery.dto;

import java.util.List;

public class BatteryRangeResponseDto {
    private List<String> batteryNames;
    private Long totalWattCapacity;
    private Double averageWattCapacity;

    public BatteryRangeResponseDto(List<String> batteryNames, long totalWattCapacity, double averageWattCapacity) {
        this.batteryNames = batteryNames;
        this.totalWattCapacity = totalWattCapacity;
        this.averageWattCapacity = averageWattCapacity;
    }

    public List<String> getBatteryNames() {
        return batteryNames;
    }

    public Long getTotalWattCapacity() {
        return totalWattCapacity;
    }

    public Double getAverageWattCapacity() {
        return averageWattCapacity;
    }
}