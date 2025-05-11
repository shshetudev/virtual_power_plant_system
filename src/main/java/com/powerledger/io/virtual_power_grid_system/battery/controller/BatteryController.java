package com.powerledger.io.virtual_power_grid_system.battery.controller;

import com.powerledger.io.virtual_power_grid_system.battery.dto.BatteryRangeResponseDto;
import com.powerledger.io.virtual_power_grid_system.battery.dto.BatteryRequestDto;
import com.powerledger.io.virtual_power_grid_system.battery.queue.BatteryRegistrationProducer;
import com.powerledger.io.virtual_power_grid_system.battery.service.BatteryService;
import com.powerledger.io.virtual_power_grid_system.common.constants.ResponseMessages;
import com.powerledger.io.virtual_power_grid_system.common.dto.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final BatteryRegistrationProducer batteryRegistrationProducer;

    public BatteryController(BatteryService batteryService, BatteryRegistrationProducer batteryRegistrationProducer) {
        this.batteryService = batteryService;
        this.batteryRegistrationProducer = batteryRegistrationProducer;
    }


    @GetMapping("/range")
    @Operation(summary = "Get batteries by postcode range",
            description = "Returns battery names (sorted) and statistics for a postcode range with optional watt capacity filters")
    @ApiResponse(responseCode = "200", description = "Batteries and statistics retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid or missing parameters")
    public ResponseEntity<Response<BatteryRangeResponseDto>> getBatteriesByPostcodeRange(
            @RequestParam @NotNull Integer from,
            @RequestParam @NotNull Integer to,
            @RequestParam(required = false) Long minWattCapacity,
            @RequestParam(required = false) Long maxWattCapacity) {
        LOG.info("Received request for batteries in postcode range: {}-{} with watt capacity filters - min: {}, max: {}",
                from, to, minWattCapacity, maxWattCapacity);
        BatteryRangeResponseDto response =
                (minWattCapacity != null || maxWattCapacity != null)
                        ? batteryService.getBatteriesByPostcodeRange(from, to, minWattCapacity, maxWattCapacity)
                        : batteryService.getBatteriesByPostcodeRange(from, to);
        return ResponseEntity.ok(Response.success(
                HttpStatus.OK.value(),
                ResponseMessages.BATTERIES_RETRIEVED_SUCCESS,
                response));
    }

    @PostMapping
    @Operation(summary = "Save multiple batteries", description = "Add a list of batteries to the system using async processing")
    @ApiResponse(responseCode = "202", description = "Battery registration request accepted for processing")
    public ResponseEntity<Response<Void>> saveBatteries(@Valid @RequestBody List<BatteryRequestDto> batteryRequests) {
        LOG.info("Received request to register {} batteries", batteryRequests.size());

        batteryRegistrationProducer.enqueueBatteryRegistrations(batteryRequests);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(
                Response.success(
                        HttpStatus.ACCEPTED.value(),
                        ResponseMessages.BATTERIES_REGISTRATION_REQUEST_ACCEPTED,
                        null));
    }
}