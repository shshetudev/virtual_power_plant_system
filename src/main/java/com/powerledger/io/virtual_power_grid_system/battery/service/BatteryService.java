package com.powerledger.io.virtual_power_grid_system.battery.service;

import com.powerledger.io.virtual_power_grid_system.battery.dto.BatteryResponseDto;
import com.powerledger.io.virtual_power_grid_system.battery.model.Battery;
import com.powerledger.io.virtual_power_grid_system.battery.dto.BatteryRequestDto;
import com.powerledger.io.virtual_power_grid_system.battery.repository.BatteryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

// todo: Introduce mapper
@Service
public class BatteryService {

    private static final Logger LOG = LoggerFactory.getLogger(BatteryService.class);
    private final BatteryRepository batteryRepository;

    public BatteryService(BatteryRepository batteryRepository) {
        this.batteryRepository = batteryRepository;
    }

    public void saveAll(List<BatteryRequestDto> batteryDtos) {
        LOG.info("Saving {} batteries", batteryDtos.size());
        List<Battery> batteries = batteryDtos.stream()
                .map(batteryDto -> new Battery(batteryDto.getName(), batteryDto.getPostcode(), batteryDto.getWattCapacity()))
                .collect(Collectors.toList());
        batteries = batteryRepository.saveAll(batteries);
        LOG.debug("Successfully saved {} batteries", batteries.size());
    }

    // todo: Introduce Page in a static class and use for null or empty page as well
    public List<BatteryResponseDto> getAllBatteries(Pageable page) {
        LOG.info("Retrieving all batteries with pagination: page={}, size={}", page.getPageNumber(), page.getPageSize());
        Page<Battery> batteries = batteryRepository.findAll(page);
        List<BatteryResponseDto> batteryResponse = batteries.getContent().stream()
                .map(battery -> new BatteryResponseDto(battery.getName(), battery.getPostcode(), battery.getWattCapacity()))
                .collect(Collectors.toList());
        LOG.debug("Retrieved {} batteries", batteryResponse.size());
        return batteryResponse;
    }
}
