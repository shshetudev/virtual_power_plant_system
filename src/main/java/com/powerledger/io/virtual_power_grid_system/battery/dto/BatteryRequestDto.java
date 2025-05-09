package com.powerledger.io.virtual_power_grid_system.battery.dto;

public class BatteryRequestDto {
    private String name;
    private int postcode;
    private long wattCapacity;

    public BatteryRequestDto() {
    }

    public BatteryRequestDto(String name, int postcode, long wattCapacity) {
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

    public void setPostcode(int postcode) {
        this.postcode = postcode;
    }

    public long getWattCapacity() {
        return wattCapacity;
    }

    public void setWattCapacity(long wattCapacity) {
        this.wattCapacity = wattCapacity;
    }
}
