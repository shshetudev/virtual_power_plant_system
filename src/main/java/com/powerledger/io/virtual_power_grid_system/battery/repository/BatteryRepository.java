package com.powerledger.io.virtual_power_grid_system.battery.repository;

import com.powerledger.io.virtual_power_grid_system.battery.model.Battery;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BatteryRepository extends JpaRepository<Battery, Long> {
}
