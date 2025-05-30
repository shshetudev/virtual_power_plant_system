package com.powerledger.io.virtual_power_grid_system.battery.unit_tests;

import com.powerledger.io.virtual_power_grid_system.battery.dto.BatteryRangeResponseDto;
import com.powerledger.io.virtual_power_grid_system.battery.dto.BatteryRequestDto;
import com.powerledger.io.virtual_power_grid_system.battery.model.Battery;
import com.powerledger.io.virtual_power_grid_system.battery.repository.BatteryRepository;
import com.powerledger.io.virtual_power_grid_system.battery.service.BatteryServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
@DisplayName("Battery Service Tests")
class BatteryServiceTest {

    @Mock
    private BatteryRepository batteryRepository;

    @InjectMocks
    private BatteryServiceImpl batteryService;

    @Nested
    @DisplayName("Save Batteries Tests")
    class SaveBatteriesTests {
        @Test
        @DisplayName("Should save multiple batteries")
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
    }

    @Nested
    @DisplayName("Postcode Range Validation Tests")
    class PostcodeRangeValidationTests {
        @ParameterizedTest(name = "From={0}, To={1} should throw exception with message: {2}")
        @CsvSource({
                "-1, 2000, 'Postcode must be a positive number'",
                "1000, -1, 'Postcode must be a positive number'",
                "2000, 1000, 'Invalid postcode range: from > to'"
        })
        void validatePostcodeRange_shouldThrowExceptionForInvalidRange(Integer from, Integer to, String expectedMessage) {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> batteryService.getBatteriesByPostcodeRange(from, to));
            assertEquals(expectedMessage, exception.getMessage());
            verify(batteryRepository, never()).findAll();
        }

        @Test
        @DisplayName("Should accept valid postcode range")
        void validatePostcodeRange_shouldAcceptValidRange() {
            Integer from = 1000;
            Integer to = 2000;
            when(batteryRepository.findAll()).thenReturn(Collections.emptyList());

            BatteryRangeResponseDto result = batteryService.getBatteriesByPostcodeRange(from, to);

            assertNotNull(result);
            verify(batteryRepository).findAll();
        }

        @Test
        @DisplayName("Should accept equal from and to values")
        void validatePostcodeRange_shouldAcceptEqualFromAndTo() {
            Integer from = 1000;
            Integer to = 1000;
            List<Battery> batteries = List.of(new Battery("Battery1", 1000, 100L));
            when(batteryRepository.findAll()).thenReturn(batteries);

            BatteryRangeResponseDto result = batteryService.getBatteriesByPostcodeRange(from, to);

            assertEquals(1, result.getBatteryNames().size());
            assertEquals("Battery1", result.getBatteryNames().get(0));
            verify(batteryRepository).findAll();
        }
    }

    @Nested
    @DisplayName("Get Batteries By Postcode Range Tests")
    class GetBatteriesByPostcodeRangeTests {
        @Test
        @DisplayName("Should return filtered and sorted batteries")
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
        @DisplayName("Should return empty result when no batteries in range")
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
        @DisplayName("Should handle empty repository")
        void getBatteriesByPostcodeRange_shouldHandleEmptyRepository() {
            when(batteryRepository.findAll()).thenReturn(Collections.emptyList());

            BatteryRangeResponseDto result = batteryService.getBatteriesByPostcodeRange(1000, 2000);

            assertTrue(result.getBatteryNames().isEmpty());
            assertEquals(0L, result.getTotalWattCapacity());
            assertEquals(0.0, result.getAverageWattCapacity());
            verify(batteryRepository).findAll();
        }

        @Test
        @DisplayName("Should sort batteries by name")
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

        @Test
        @DisplayName("Should correctly filter batteries on boundary values")
        void getBatteriesByPostcodeRange_shouldCorrectlyFilterBatteriesOnBoundaries() {
            Integer from = 1000;
            Integer to = 2000;

            List<Battery> allBatteries = Arrays.asList(
                    new Battery("BatteryBelow", 999, 100L),      // Just below lower boundary
                    new Battery("BatteryLower", 1000, 150L),     // Exactly at lower boundary
                    new Battery("BatteryMiddle", 1500, 200L),    // In the middle of range
                    new Battery("BatteryUpper", 2000, 250L),     // Exactly at upper boundary
                    new Battery("BatteryAbove", 2001, 300L)      // Just above upper boundary
            );

            when(batteryRepository.findAll()).thenReturn(allBatteries);

            BatteryRangeResponseDto result = batteryService.getBatteriesByPostcodeRange(from, to);

            assertEquals(3, result.getBatteryNames().size());
            assertTrue(result.getBatteryNames().contains("BatteryLower"));
            assertTrue(result.getBatteryNames().contains("BatteryMiddle"));
            assertTrue(result.getBatteryNames().contains("BatteryUpper"));
            assertFalse(result.getBatteryNames().contains("BatteryBelow"));
            assertFalse(result.getBatteryNames().contains("BatteryAbove"));

            assertEquals(600L, result.getTotalWattCapacity());
            assertEquals(200.0, result.getAverageWattCapacity());
            verify(batteryRepository).findAll();
        }
    }

    @Nested
    @DisplayName("Watt Capacity Filtering Tests")
    class WattCapacityFilteringTests {

        @Test
        @DisplayName("Should filter batteries by minimum watt capacity")
        void getBatteriesByPostcodeRange_shouldFilterByMinWattCapacity() {
            List<Battery> allBatteries = Arrays.asList(
                    new Battery("LowWatt", 1500, 100L),
                    new Battery("MidWatt", 1500, 200L),
                    new Battery("HighWatt", 1500, 300L)
            );
            when(batteryRepository.findAll()).thenReturn(allBatteries);

            BatteryRangeResponseDto result = batteryService.getBatteriesByPostcodeRange(1000, 2000, 200L, null);

            assertEquals(2, result.getBatteryNames().size());
            assertTrue(result.getBatteryNames().contains("MidWatt"));
            assertTrue(result.getBatteryNames().contains("HighWatt"));
            assertFalse(result.getBatteryNames().contains("LowWatt"));
            assertEquals(500L, result.getTotalWattCapacity());
            assertEquals(250.0, result.getAverageWattCapacity());
            verify(batteryRepository).findAll();
        }

        @Test
        @DisplayName("Should filter batteries by maximum watt capacity")
        void getBatteriesByPostcodeRange_shouldFilterByMaxWattCapacity() {
            List<Battery> allBatteries = Arrays.asList(
                    new Battery("LowWatt", 1500, 100L),
                    new Battery("MidWatt", 1500, 200L),
                    new Battery("HighWatt", 1500, 300L)
            );
            when(batteryRepository.findAll()).thenReturn(allBatteries);

            BatteryRangeResponseDto result = batteryService.getBatteriesByPostcodeRange(1000, 2000, null, 200L);

            assertEquals(2, result.getBatteryNames().size());
            assertTrue(result.getBatteryNames().contains("LowWatt"));
            assertTrue(result.getBatteryNames().contains("MidWatt"));
            assertFalse(result.getBatteryNames().contains("HighWatt"));
            assertEquals(300L, result.getTotalWattCapacity());
            assertEquals(150.0, result.getAverageWattCapacity());
            verify(batteryRepository).findAll();
        }

        @Test
        @DisplayName("Should filter batteries by both min and max watt capacity")
        void getBatteriesByPostcodeRange_shouldFilterByMinAndMaxWattCapacity() {
            List<Battery> allBatteries = Arrays.asList(
                    new Battery("VeryLowWatt", 1500, 50L),
                    new Battery("LowWatt", 1500, 100L),
                    new Battery("MidWatt", 1500, 200L),
                    new Battery("HighWatt", 1500, 300L),
                    new Battery("VeryHighWatt", 1500, 400L)
            );
            when(batteryRepository.findAll()).thenReturn(allBatteries);

            BatteryRangeResponseDto result = batteryService.getBatteriesByPostcodeRange(1000, 2000, 100L, 300L);

            assertEquals(3, result.getBatteryNames().size());
            assertTrue(result.getBatteryNames().contains("LowWatt"));
            assertTrue(result.getBatteryNames().contains("MidWatt"));
            assertTrue(result.getBatteryNames().contains("HighWatt"));
            assertFalse(result.getBatteryNames().contains("VeryLowWatt"));
            assertFalse(result.getBatteryNames().contains("VeryHighWatt"));
            assertEquals(600L, result.getTotalWattCapacity());
            assertEquals(200.0, result.getAverageWattCapacity());
            verify(batteryRepository).findAll();
        }

        @Test
        @DisplayName("Should combine postcode and watt capacity filters")
        void getBatteriesByPostcodeRange_shouldCombinePostcodeAndWattCapacityFilters() {
            List<Battery> allBatteries = Arrays.asList(
                    new Battery("OutsidePostcode", 2500, 200L),
                    new Battery("LowWatt", 1500, 100L),
                    new Battery("ValidBattery", 1800, 250L),
                    new Battery("HighWatt", 1200, 350L)
            );
            when(batteryRepository.findAll()).thenReturn(allBatteries);

            // Act
            BatteryRangeResponseDto result = batteryService.getBatteriesByPostcodeRange(1000, 2000, 150L, 300L);

            // Assert
            assertEquals(1, result.getBatteryNames().size());
            assertEquals("ValidBattery", result.getBatteryNames().get(0));
            assertEquals(250L, result.getTotalWattCapacity());
            assertEquals(250.0, result.getAverageWattCapacity());
            verify(batteryRepository).findAll();
        }

        @ParameterizedTest(name = "Min={0}, Max={1} should throw exception with message: {2}")
        @CsvSource({
                "-100, 200, 'Watt capacity must be a positive number'",
                "100, -200, 'Watt capacity must be a positive number'",
                "300, 200, 'Watt capacity range: from > to'"
        })
        @DisplayName("Should validate watt capacity parameters")
        void validateWattCapacity_shouldThrowExceptionForInvalidValues(Long min, Long max, String expectedMessage) {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> batteryService.getBatteriesByPostcodeRange(1000, 2000, min, max));
            assertEquals(expectedMessage, exception.getMessage());
            verify(batteryRepository, never()).findAll();
        }

        @Test
        @DisplayName("Should accept null values for watt capacity filters")
        void validateWattCapacity_shouldAcceptNullValues() {
            List<Battery> allBatteries = Arrays.asList(
                    new Battery("Battery1", 1500, 100L),
                    new Battery("Battery2", 1500, 200L)
            );
            when(batteryRepository.findAll()).thenReturn(allBatteries);


            BatteryRangeResponseDto result = batteryService.getBatteriesByPostcodeRange(1000, 2000, null, null);

            assertEquals(2, result.getBatteryNames().size());
            verify(batteryRepository).findAll();
        }

        @Test
        @DisplayName("Should handle empty result after watt filtering")
        void getBatteriesByPostcodeRange_shouldHandleEmptyResultAfterWattFiltering() {
            // Arrange
            List<Battery> allBatteries = Arrays.asList(
                    new Battery("LowWatt", 1500, 100L),
                    new Battery("MidWatt", 1500, 200L)
            );
            when(batteryRepository.findAll()).thenReturn(allBatteries);

            BatteryRangeResponseDto result = batteryService.getBatteriesByPostcodeRange(1000, 2000, 500L, null);

            assertTrue(result.getBatteryNames().isEmpty());
            assertEquals(0L, result.getTotalWattCapacity());
            assertEquals(0.0, result.getAverageWattCapacity());
            verify(batteryRepository).findAll();
        }
    }
}