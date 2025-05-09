package com.powerledger.io.virtual_power_grid_system.battery.repository;

import com.powerledger.io.virtual_power_grid_system.battery.model.Battery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BatteryRepository extends JpaRepository<Battery, Long> {
}
