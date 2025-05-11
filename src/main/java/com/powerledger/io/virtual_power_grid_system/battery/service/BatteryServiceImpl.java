package com.powerledger.io.virtual_power_grid_system.battery.service;

import com.powerledger.io.virtual_power_grid_system.battery.dto.BatteryRangeResponseDto;
import com.powerledger.io.virtual_power_grid_system.battery.model.Battery;
import com.powerledger.io.virtual_power_grid_system.battery.dto.BatteryRequestDto;
import com.powerledger.io.virtual_power_grid_system.battery.repository.BatteryRepository;
import com.powerledger.io.virtual_power_grid_system.common.constants.ResponseMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class BatteryServiceImpl implements BatteryService {

    private static final Logger LOG = LoggerFactory.getLogger(BatteryServiceImpl.class);
    private final BatteryRepository batteryRepository;

    public BatteryServiceImpl(BatteryRepository batteryRepository) {
        this.batteryRepository = batteryRepository;
    }

    @Override
    public void saveAll(List<BatteryRequestDto> batteryDtos) {
        LOG.info("Saving {} batteries", batteryDtos.size());
        List<Battery> batteries = batteryDtos.stream().map(batteryDto -> new Battery(batteryDto.getName(), batteryDto.getPostcode(), batteryDto.getWattCapacity())).toList();
        batteries = batteryRepository.saveAll(batteries);
        LOG.debug("Successfully saved {} batteries", batteries.size());
    }

    @Override
    public BatteryRangeResponseDto getBatteriesByPostcodeRange(Integer from, Integer to,
                                                               Long minWattCapacity,
                                                               Long maxWattCapacity) {
        LOG.info("Retrieving batteries with postcode range: from={}, to={}, minWatt={}, maxWatt={}",
                from, to, minWattCapacity, maxWattCapacity);
        validateWattCapacity(minWattCapacity, maxWattCapacity);

        // todo: Consider using a range query in the repository for more efficient data retrieval, it's kept just to show the usages of steam api
        List<Battery> batteries = batteryRepository.findAll().stream()
                .filter(b -> b.getPostcode() >= from && b.getPostcode() <= to)
                .filter(b -> minWattCapacity == null || b.getWattCapacity() >= minWattCapacity)
                .filter(b -> maxWattCapacity == null || b.getWattCapacity() <= maxWattCapacity)
                .sorted(Comparator.comparing(Battery::getName))
                .toList();

        LOG.debug("Retrieved {} batteries", batteries.size());
        List<String> names = batteries.stream().map(Battery::getName).toList();
        long total = batteries.stream().mapToLong(Battery::getWattCapacity).sum();
        double average = batteries.isEmpty() ? 0.0 : batteries.stream()
                .mapToLong(Battery::getWattCapacity)
                .average()
                .orElse(0.0);

        return new BatteryRangeResponseDto(names, total, average);
    }

    @Override
    public BatteryRangeResponseDto getBatteriesByPostcodeRange(Integer from, Integer to) {
        validatePostcodeRange(from, to);
        return getBatteriesByPostcodeRange(from, to, null, null);
    }

    private void validatePostcodeRange(Integer from, Integer to) {
        if (from < 0 || to < 0) {
            throw new IllegalArgumentException(ResponseMessages.POSTCODE_MUST_BE_POSITIVE);
        }
        if (from > to) {
            throw new IllegalArgumentException(ResponseMessages.POSTCODE_RANGE_INVALID);
        }
    }

    private void validateWattCapacity(Long minWattCapacity, Long maxWattCapacity) {
        if (minWattCapacity == null && maxWattCapacity == null) {
            return;
        }

        if ((minWattCapacity != null && minWattCapacity < 0) ||
                (maxWattCapacity != null && maxWattCapacity < 0)) {
            throw new IllegalArgumentException(ResponseMessages.WATT_CAPACITY_MUST_BE_POSITIVE);
        }

        if ((minWattCapacity != null && maxWattCapacity != null) && (minWattCapacity > maxWattCapacity)) {
            throw new IllegalArgumentException(ResponseMessages.WATT_CAPACITY_RANGE_INVALID);
        }
    }
}
