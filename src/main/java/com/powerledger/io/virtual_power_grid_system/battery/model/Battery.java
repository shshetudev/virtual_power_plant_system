package com.powerledger.io.virtual_power_grid_system.battery.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Battery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Integer postcode;
    private Long wattCapacity;

    public Battery() {
    }

    public Battery(String name, Integer postcode, Long wattCapacity) {
        this.name = name;
        this.postcode = postcode;
        this.wattCapacity = wattCapacity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPostcode() {
        return postcode;
    }

    public void setPostcode(Integer postcode) {
        this.postcode = postcode;
    }

    public Long getWattCapacity() {
        return wattCapacity;
    }

    public void setWattCapacity(Long wattCapacity) {
        this.wattCapacity = wattCapacity;
    }
}
