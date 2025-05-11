package com.powerledger.io.virtual_power_grid_system.battery.unit_tests;

import com.powerledger.io.virtual_power_grid_system.battery.dto.BatteryRangeResponseDto;
import com.powerledger.io.virtual_power_grid_system.battery.dto.BatteryRequestDto;
import com.powerledger.io.virtual_power_grid_system.battery.model.Battery;
import com.powerledger.io.virtual_power_grid_system.battery.repository.BatteryRepository;
import com.powerledger.io.virtual_power_grid_system.battery.service.BatteryServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BatteryServiceImplTest {

    @Mock
    private BatteryRepository batteryRepository;

    @InjectMocks
    private BatteryServiceImpl batteryService;

    @Test
    void saveAll_shouldSaveBatteries() {
        List<BatteryRequestDto> batteryDtos = Arrays.asList(
                new BatteryRequestDto("Battery1", 1000, 100L),
                new BatteryRequestDto("Battery2", 2000, 200L)
        );

        List<Battery> expectedBatteries = Arrays.asList(
                new Battery("Battery1", 1000, 100L),
                new Battery("Battery2", 2000, 200L)
        );

        when(batteryRepository.saveAll(anyList())).thenReturn(expectedBatteries);

        batteryService.saveAll(batteryDtos);

        verify(batteryRepository).saveAll(anyList());
    }

    @Test
    void getBatteriesByPostcodeRange_shouldReturnFilteredAndSortedBatteries() {
        List<Battery> allBatteries = Arrays.asList(
                new Battery("ZBattery", 2000, 200L),
                new Battery("ABattery", 1000, 100L),
                new Battery("MBattery", 1500, 150L),
                new Battery("OutOfRange", 3000, 300L)
        );

        when(batteryRepository.findAll()).thenReturn(allBatteries);

        BatteryRangeResponseDto result = batteryService.getBatteriesByPostcodeRange(1000, 2000);

        assertEquals(List.of("ABattery", "MBattery", "ZBattery"), result.getBatteryNames());
        assertEquals(450L, result.getTotalWattCapacity());
        assertEquals(150.0, result.getAverageWattCapacity());
        verify(batteryRepository).findAll();
    }

    @Test
    void getBatteriesByPostcodeRange_shouldReturnEmptyResult() {
        List<Battery> allBatteries = Arrays.asList(
                new Battery("Battery1", 3000, 100L),
                new Battery("Battery2", 4000, 200L)
        );

        when(batteryRepository.findAll()).thenReturn(allBatteries);

        BatteryRangeResponseDto result = batteryService.getBatteriesByPostcodeRange(1000, 2000);

        assertTrue(result.getBatteryNames().isEmpty());
        assertEquals(0L, result.getTotalWattCapacity());
        assertEquals(0.0, result.getAverageWattCapacity());
        verify(batteryRepository).findAll();
    }

    @Test
    void getBatteriesByPostcodeRange_shouldHandleEmptyRepository() {
        when(batteryRepository.findAll()).thenReturn(Collections.emptyList());

        BatteryRangeResponseDto result = batteryService.getBatteriesByPostcodeRange(1000, 2000);

        assertTrue(result.getBatteryNames().isEmpty());
        assertEquals(0L, result.getTotalWattCapacity());
        assertEquals(0.0, result.getAverageWattCapacity());
        verify(batteryRepository).findAll();
    }

    @Test
    void getBatteriesByPostcodeRange_shouldSortByName() {
        List<Battery> allBatteries = Arrays.asList(
                new Battery("ZBattery", 1500, 150L),
                new Battery("ABattery", 1500, 150L),
                new Battery("MBattery", 1500, 150L)
        );

        when(batteryRepository.findAll()).thenReturn(allBatteries);

        BatteryRangeResponseDto result = batteryService.getBatteriesByPostcodeRange(1000, 2000);

        assertEquals(List.of("ABattery", "MBattery", "ZBattery"), result.getBatteryNames());
        verify(batteryRepository).findAll();
    }
}