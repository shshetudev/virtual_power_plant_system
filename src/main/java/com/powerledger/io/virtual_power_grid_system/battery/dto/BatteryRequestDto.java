package com.powerledger.io.virtual_power_grid_system.battery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class BatteryRequestDto {
    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Postcode is required")
    @Positive(message = "Postcode must be a positive number")
    private Integer postcode;

    @NotNull(message = "Watt capacity is required")
    @Positive(message = "Watt capacity must be a positive number")
    private Long wattCapacity;

    public BatteryRequestDto() {
    }

    public BatteryRequestDto(String name, Integer postcode, Long wattCapacity) {
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
