package com.powerledger.io.virtual_power_grid_system.battery.dto;

public class BatteryResponseDto {
    private String name;
    private Integer postcode;
    private Long wattCapacity;

    public BatteryResponseDto() {
    }

    public BatteryResponseDto(String name, Integer postcode, Long wattCapacity) {
        this.name = name;
        this.postcode = postcode;
        this.wattCapacity = wattCapacity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPostcode() {
        return postcode;
    }

    public void setPostcode(Integer postcode) {
        this.postcode = postcode;
    }

    public long getWattCapacity() {
        return wattCapacity;
    }

    public void setWattCapacity(Long wattCapacity) {
        this.wattCapacity = wattCapacity;
    }
}
