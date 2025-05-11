package com.powerledger.io.virtual_power_grid_system.battery.integration_tests;

import com.powerledger.io.virtual_power_grid_system.battery.model.Battery;
import com.powerledger.io.virtual_power_grid_system.battery.repository.BatteryRepository;
import com.powerledger.io.virtual_power_grid_system.common.integration_tests.BaseRepositoryIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BatteryRepositoryIntegrationTest extends BaseRepositoryIntegrationTest {

    @Autowired
    private BatteryRepository batteryRepository;

    @BeforeEach
    void setup() {
        batteryRepository.deleteAll();

        List<Battery> batteries = Arrays.asList(
                new Battery("Battery A", 1000, 200L),
                new Battery("Battery B", 1500, 300L),
                new Battery("Battery C", 2000, 100L),
                new Battery("Battery D", 2500, 400L)
        );

        batteryRepository.saveAll(batteries);
    }

    @Test
    @DisplayName("Should find all batteries ordered by name")
    void findAll_shouldReturnAllBatteriesOrderedByName() {
        List<Battery> result = batteryRepository.findAll(Sort.by("name"));

        assertThat(result).hasSize(4);
        assertThat(result.get(0).getName()).isEqualTo("Battery A");
        assertThat(result.get(1).getName()).isEqualTo("Battery B");
        assertThat(result.get(2).getName()).isEqualTo("Battery C");
        assertThat(result.get(3).getName()).isEqualTo("Battery D");
    }

    @Test
    @DisplayName("Should find batteries within postcode range using stream filtering")
    void findAll_withPostcodeRangeFiltering_shouldReturnBatteriesInRange() {
        int fromPostcode = 1000;
        int toPostcode = 2000;

        List<Battery> result = batteryRepository.findAll().stream()
                .filter(battery -> battery.getPostcode() >= fromPostcode && battery.getPostcode() <= toPostcode)
                .sorted(java.util.Comparator.comparing(Battery::getName))
                .collect(Collectors.toList());

        assertThat(result).hasSize(3);
        assertThat(result.get(0).getName()).isEqualTo("Battery A");
        assertThat(result.get(1).getName()).isEqualTo("Battery B");
        assertThat(result.get(2).getName()).isEqualTo("Battery C");
    }

    @Test
    @DisplayName("Should find batteries with watt capacity constraints using stream filtering")
    void findAll_withWattCapacityFiltering_shouldFilterByWattCapacity() {
        int fromPostcode = 1000;
        int toPostcode = 2500;
        Long minWattCapacity = 200L;
        Long maxWattCapacity = 350L;

        List<Battery> result = batteryRepository.findAll().stream()
                .filter(battery -> battery.getPostcode() >= fromPostcode && battery.getPostcode() <= toPostcode)
                .filter(battery -> battery.getWattCapacity() >= minWattCapacity && battery.getWattCapacity() <= maxWattCapacity)
                .sorted(java.util.Comparator.comparing(Battery::getName))
                .toList();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Battery A");
        assertThat(result.get(1).getName()).isEqualTo("Battery B");
    }
}