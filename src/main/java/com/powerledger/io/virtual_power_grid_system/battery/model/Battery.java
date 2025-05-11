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

    private Battery(Builder builder) {
        this.name = builder.name;
        this.postcode = builder.postcode;
        this.wattCapacity = builder.wattCapacity;
    }

    public Battery() {
    }

    public Battery(String name, Integer postcode, Long wattCapacity) {
        this.name = name;
        this.postcode = postcode;
        this.wattCapacity = wattCapacity;
    }

    public static Builder builder() {
        return new Builder();
    }

    // Builder
    public static class Builder {
        private String name;
        private Integer postcode;
        private Long wattCapacity;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder postcode(Integer postcode) {
            this.postcode = postcode;
            return this;
        }

        public Builder wattCapacity(Long wattCapacity) {
            this.wattCapacity = wattCapacity;
            return this;
        }

        public Battery build() {
            return new Battery(this);
        }
    }

    // Getters and setters
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