package com.powerledger.io.virtual_power_grid_system.battery.integration_tests;

import com.powerledger.io.virtual_power_grid_system.battery.dto.BatteryRangeResponseDto;
import com.powerledger.io.virtual_power_grid_system.battery.dto.BatteryRequestDto;
import com.powerledger.io.virtual_power_grid_system.battery.model.Battery;
import com.powerledger.io.virtual_power_grid_system.battery.repository.BatteryRepository;
import com.powerledger.io.virtual_power_grid_system.battery.service.BatteryService;
import com.powerledger.io.virtual_power_grid_system.common.constants.ResponseMessages;
import com.powerledger.io.virtual_power_grid_system.common.integration_tests.BaseRepositoryIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class BatteryServiceIntegrationTest extends BaseRepositoryIntegrationTest {

    @Autowired
    private BatteryService batteryService;

    @Autowired
    private BatteryRepository batteryRepository;

    @BeforeEach
    void setup() {
        batteryRepository.deleteAll();
    }

    @Test
    @DisplayName("Should save all battery requests")
    void saveAll_shouldPersistAllBatteries() {
        List<BatteryRequestDto> batteryRequests = Arrays.asList(
            new BatteryRequestDto("Battery X", 1000, 200L),
            new BatteryRequestDto("Battery Y", 1500, 300L),
            new BatteryRequestDto("Battery Z", 2000, 400L)
        );

        batteryService.saveAll(batteryRequests);

        List<Battery> savedBatteries = batteryRepository.findAll();
        assertThat(savedBatteries).hasSize(3);
        assertThat(savedBatteries.stream().map(Battery::getName).toList())
            .containsExactlyInAnyOrder("Battery X", "Battery Y", "Battery Z");
    }

    @Test
    @DisplayName("Should retrieve batteries by postcode range")
    void getBatteriesByPostcodeRange_shouldReturnFilteredBatteries() {
        List<BatteryRequestDto> batteryRequests = Arrays.asList(
            new BatteryRequestDto("Battery A", 1000, 200L),
            new BatteryRequestDto("Battery B", 1500, 300L),
            new BatteryRequestDto("Battery C", 2000, 100L),
            new BatteryRequestDto("Battery D", 2500, 400L)
        );
        batteryService.saveAll(batteryRequests);

        BatteryRangeResponseDto response = batteryService.getBatteriesByPostcodeRange(1000, 2000);

        assertThat(response.getBatteryNames()).hasSize(3);
        assertThat(response.getBatteryNames()).containsExactly("Battery A", "Battery B", "Battery C");
        assertEquals(600L, response.getTotalWattCapacity());
        assertEquals(200.0, response.getAverageWattCapacity());
    }

    @Test
    @DisplayName("Should retrieve batteries by postcode range with watt capacity constraints")
    void getBatteriesByPostcodeRange_withWattCapacityFilter_shouldReturnFilteredBatteries() {
        List<BatteryRequestDto> batteryRequests = Arrays.asList(
            new BatteryRequestDto("Battery A", 1000, 200L),
            new BatteryRequestDto("Battery B", 1500, 300L),
            new BatteryRequestDto("Battery C", 2000, 100L),
            new BatteryRequestDto("Battery D", 2500, 400L)
        );
        batteryService.saveAll(batteryRequests);

        BatteryRangeResponseDto response = batteryService.getBatteriesByPostcodeRange(1000, 2500, 200L, 350L);

        assertThat(response.getBatteryNames()).hasSize(2);
        assertThat(response.getBatteryNames()).containsExactly("Battery A", "Battery B");
        assertEquals(500L, response.getTotalWattCapacity());
        assertEquals(250.0, response.getAverageWattCapacity());
    }

    @Test
    @DisplayName("Should throw exception for invalid postcode range")
    void getBatteriesByPostcodeRange_withInvalidRange_shouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> batteryService.getBatteriesByPostcodeRange(2000, 1000));

        assertEquals(ResponseMessages.POSTCODE_RANGE_INVALID, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for negative postcode")
    void getBatteriesByPostcodeRange_withNegativePostcode_shouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> batteryService.getBatteriesByPostcodeRange(-1000, 2000));

        assertEquals(ResponseMessages.POSTCODE_MUST_BE_POSITIVE, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for invalid watt capacity range")
    void getBatteriesByPostcodeRange_withInvalidWattCapacityRange_shouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> batteryService.getBatteriesByPostcodeRange(1000, 2000, 300L, 200L));

        assertEquals(ResponseMessages.WATT_CAPACITY_RANGE_INVALID, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for negative watt capacity")
    void getBatteriesByPostcodeRange_withNegativeWattCapacity_shouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> batteryService.getBatteriesByPostcodeRange(1000, 2000, -100L, 200L));

        assertEquals(ResponseMessages.WATT_CAPACITY_MUST_BE_POSITIVE, exception.getMessage());
    }

    @Test
    @DisplayName("Should return empty result when no batteries match criteria")
    void getBatteriesByPostcodeRange_withNoMatches_shouldReturnEmptyResult() {
        List<BatteryRequestDto> batteryRequests = Arrays.asList(
            new BatteryRequestDto("Battery A", 1000, 200L),
            new BatteryRequestDto("Battery B", 1500, 300L)
        );
        batteryService.saveAll(batteryRequests);

        BatteryRangeResponseDto response = batteryService.getBatteriesByPostcodeRange(3000, 4000);

        assertThat(response.getBatteryNames()).isEmpty();
        assertEquals(0L, response.getTotalWattCapacity());
        assertEquals(0.0, response.getAverageWattCapacity());
    }
}