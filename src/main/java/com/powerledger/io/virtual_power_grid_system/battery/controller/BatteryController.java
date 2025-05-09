package com.powerledger.io.virtual_power_grid_system.battery.controller;

import com.powerledger.io.virtual_power_grid_system.battery.dto.BatteryRequestDto;
import com.powerledger.io.virtual_power_grid_system.battery.dto.BatteryResponseDto;
import com.powerledger.io.virtual_power_grid_system.battery.service.BatteryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/batteries")
@Tag(name = "Battery Management", description = "API for managing batteries in the Virtual Power Grid System")
public class BatteryController {

    private static final Logger LOG = LoggerFactory.getLogger(BatteryController.class);
    private final BatteryService batteryService;

    public BatteryController(BatteryService batteryService) {
        this.batteryService = batteryService;
    }

    @PostMapping
    @Operation(summary = "Save multiple batteries", description = "Add a list of batteries to the system")
    @ApiResponse(responseCode = "201", description = "Batteries successfully created")
    public ResponseEntity<Void> saveBatteries(@Valid @RequestBody List<BatteryRequestDto> batteryRequests) {
        LOG.info("Received request to save {} batteries", batteryRequests.size());
        batteryService.saveAll(batteryRequests);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    @Operation(summary = "Get all batteries", description = "Retrieve all batteries with pagination and sorting")
    @ApiResponse(responseCode = "200", description = "List of batteries retrieved successfully")
    public ResponseEntity<List<BatteryResponseDto>> getAllBatteries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy) {

        LOG.info("Received request to get batteries: page={}, size={}, sortBy={}", page, size, sortBy);

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortBy));
        List<BatteryResponseDto> batteries = batteryService.getAllBatteries(pageRequest);

        return ResponseEntity.ok(batteries);
    }
}