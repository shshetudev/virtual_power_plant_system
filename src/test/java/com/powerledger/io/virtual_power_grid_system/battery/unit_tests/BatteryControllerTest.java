package com.powerledger.io.virtual_power_grid_system.battery.unit_tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.powerledger.io.virtual_power_grid_system.battery.controller.BatteryController;
import com.powerledger.io.virtual_power_grid_system.battery.dto.BatteryRangeResponseDto;
import com.powerledger.io.virtual_power_grid_system.battery.dto.BatteryRequestDto;
import com.powerledger.io.virtual_power_grid_system.battery.queue.BatteryRegistrationProducer;
import com.powerledger.io.virtual_power_grid_system.battery.service.BatteryService;
import com.powerledger.io.virtual_power_grid_system.common.constants.ResponseMessages;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BatteryController.class)
@DisplayName("Battery Controller Tests")
class BatteryControllerTest {

    private static final String BASE_PATH = "/api/v1/batteries";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BatteryService batteryService;

    @MockitoBean
    private BatteryRegistrationProducer batteryRegistrationProducer;

    @Nested
    @DisplayName("Save Batteries Endpoint Tests")
    class SaveBatteriesTests {

        @Test
        @DisplayName("Should save batteries and return created status")
        void saveBatteries_shouldSaveBatteriesAndReturnCreatedStatus() throws Exception {
            List<BatteryRequestDto> batteryRequests = Arrays.asList(
                    new BatteryRequestDto("Battery1", 1000, 100L),
                    new BatteryRequestDto("Battery2", 2000, 200L)
            );

            mockMvc.perform(post(BASE_PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(batteryRequests)))
                    .andExpect(status().isAccepted())
                    .andExpect(jsonPath("$.status").value(202))
                    .andExpect(jsonPath("$.message").value(ResponseMessages.BATTERIES_REGISTRATION_REQUEST_ACCEPTED))
                    .andExpect(jsonPath("$.data").isEmpty());
        }

        @Test
        @DisplayName("Should return error when request is invalid")
        void saveBatteries_shouldReturnErrorWhenRequestIsInvalid() throws Exception {
            String invalidJson = """
                    [
                      {"name": "", "postcode": null, "wattCapacity": null}
                    ]
                    """;

            mockMvc.perform(post(BASE_PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value(ResponseMessages.VALIDATION_ERROR));
        }

        @Test
        @DisplayName("Should handle empty list")
        void saveBatteries_shouldHandleEmptyList() throws Exception {
            List<BatteryRequestDto> emptyList = Collections.emptyList();

            mockMvc.perform(post(BASE_PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(emptyList)))
                    .andExpect(status().isAccepted())
                    .andExpect(jsonPath("$.status").value(202))
                    .andExpect(jsonPath("$.message").value(ResponseMessages.BATTERIES_REGISTRATION_REQUEST_ACCEPTED));
        }

        @Test
        @DisplayName("Should validate each item in list")
        void saveBatteries_shouldValidateEachItemInList() throws Exception {
            List<BatteryRequestDto> mixedValidityList = Arrays.asList(
                    new BatteryRequestDto("Valid", 1000, 100L),
                    new BatteryRequestDto("", 2000, 200L)
            );

            mockMvc.perform(post(BASE_PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(mixedValidityList)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value(ResponseMessages.VALIDATION_ERROR));
        }
    }

    @Nested
    @DisplayName("Get Batteries By Postcode Range Endpoint Tests")
    class GetBatteriesByPostcodeRangeTests {

        @Test
        @DisplayName("Should return batteries in range")
        void getBatteriesByPostcodeRange_shouldReturnBatteriesInRange() throws Exception {
            BatteryRangeResponseDto responseDto = new BatteryRangeResponseDto(
                    Arrays.asList("Battery1", "Battery2"),
                    300L,
                    150.0
            );

            when(batteryService.getBatteriesByPostcodeRange(anyInt(), anyInt())).thenReturn(responseDto);

            mockMvc.perform(get(BASE_PATH + "/range")
                            .param("from", "1000")
                            .param("to", "2000"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.message").value(ResponseMessages.BATTERIES_RETRIEVED_SUCCESS))
                    .andExpect(jsonPath("$.data.batteryNames[0]").value("Battery1"))
                    .andExpect(jsonPath("$.data.batteryNames[1]").value("Battery2"))
                    .andExpect(jsonPath("$.data.totalWattCapacity").value(300))
                    .andExpect(jsonPath("$.data.averageWattCapacity").value(150.0));

            verify(batteryService).getBatteriesByPostcodeRange(1000, 2000);
        }

        @Test
        @DisplayName("Should return error when parameters are missing")
        void getBatteriesByPostcodeRange_shouldReturnErrorWhenParametersAreMissing() throws Exception {
            mockMvc.perform(get(BASE_PATH + "/range"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value(ResponseMessages.MISSING_REQUIRED_PARAMETER));
        }

        @Test
        @DisplayName("Should return empty result")
        void getBatteriesByPostcodeRange_shouldReturnEmptyResult() throws Exception {
            BatteryRangeResponseDto emptyResponse = new BatteryRangeResponseDto(
                    Collections.emptyList(),
                    0L,
                    0.0
            );

            when(batteryService.getBatteriesByPostcodeRange(anyInt(), anyInt())).thenReturn(emptyResponse);

            mockMvc.perform(get(BASE_PATH + "/range")
                            .param("from", "1000")
                            .param("to", "2000"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.message").value(ResponseMessages.BATTERIES_RETRIEVED_SUCCESS))
                    .andExpect(jsonPath("$.data").exists())
                    .andExpect(jsonPath("$.data.batteryNames").isArray())
                    .andExpect(jsonPath("$.data.batteryNames").isEmpty())
                    .andExpect(jsonPath("$.data.totalWattCapacity").value(0))
                    .andExpect(jsonPath("$.data.averageWattCapacity").value(0.0));

            verify(batteryService).getBatteriesByPostcodeRange(1000, 2000);
        }

        @Test
        @DisplayName("Should handle invalid range values")
        void getBatteriesByPostcodeRange_shouldHandleInvalidRangeValues() throws Exception {
            when(batteryService.getBatteriesByPostcodeRange(2000, 1000))
                    .thenThrow(new IllegalArgumentException(ResponseMessages.POSTCODE_RANGE_INVALID));

            mockMvc.perform(get(BASE_PATH + "/range")
                            .param("from", "2000")
                            .param("to", "1000"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value(ResponseMessages.INVALID_ARGUMENT));

            verify(batteryService).getBatteriesByPostcodeRange(2000, 1000);
        }
    }

    @Nested
    @DisplayName("Get Batteries With Watt Capacity Filters Tests")
    class GetBatteriesWithWattCapacityFiltersTests {

        @Test
        @DisplayName("Should filter batteries by minimum watt capacity")
        void getBatteriesByPostcodeRange_shouldFilterByMinWattCapacity() throws Exception {
            BatteryRangeResponseDto responseDto = new BatteryRangeResponseDto(
                    Arrays.asList("HighWatt", "MidWatt"),
                    500L,
                    250.0
            );

            when(batteryService.getBatteriesByPostcodeRange(anyInt(), anyInt(), eq(200L), isNull()))
                    .thenReturn(responseDto);

            mockMvc.perform(get(BASE_PATH + "/range")
                            .param("from", "1000")
                            .param("to", "2000")
                            .param("minWattCapacity", "200"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.message").value(ResponseMessages.BATTERIES_RETRIEVED_SUCCESS))
                    .andExpect(jsonPath("$.data.batteryNames").isArray())
                    .andExpect(jsonPath("$.data.batteryNames[0]").value("HighWatt"))
                    .andExpect(jsonPath("$.data.batteryNames[1]").value("MidWatt"))
                    .andExpect(jsonPath("$.data.totalWattCapacity").value(500))
                    .andExpect(jsonPath("$.data.averageWattCapacity").value(250.0));

            verify(batteryService).getBatteriesByPostcodeRange(1000, 2000, 200L, null);
        }

        @Test
        @DisplayName("Should filter batteries by maximum watt capacity")
        void getBatteriesByPostcodeRange_shouldFilterByMaxWattCapacity() throws Exception {
            BatteryRangeResponseDto responseDto = new BatteryRangeResponseDto(
                    Arrays.asList("LowWatt", "MidWatt"),
                    300L,
                    150.0
            );

            when(batteryService.getBatteriesByPostcodeRange(anyInt(), anyInt(), isNull(), eq(200L)))
                    .thenReturn(responseDto);

            mockMvc.perform(get(BASE_PATH + "/range")
                            .param("from", "1000")
                            .param("to", "2000")
                            .param("maxWattCapacity", "200"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.data.batteryNames[0]").value("LowWatt"))
                    .andExpect(jsonPath("$.data.batteryNames[1]").value("MidWatt"))
                    .andExpect(jsonPath("$.data.totalWattCapacity").value(300))
                    .andExpect(jsonPath("$.data.averageWattCapacity").value(150.0));

            verify(batteryService).getBatteriesByPostcodeRange(1000, 2000, null, 200L);
        }

        @Test
        @DisplayName("Should filter batteries by both min and max watt capacity")
        void getBatteriesByPostcodeRange_shouldFilterByMinAndMaxWattCapacity() throws Exception {
            BatteryRangeResponseDto responseDto = new BatteryRangeResponseDto(
                    List.of("MidWatt"),
                    200L,
                    200.0
            );

            when(batteryService.getBatteriesByPostcodeRange(anyInt(), anyInt(), eq(150L), eq(250L)))
                    .thenReturn(responseDto);

            mockMvc.perform(get(BASE_PATH + "/range")
                            .param("from", "1000")
                            .param("to", "2000")
                            .param("minWattCapacity", "150")
                            .param("maxWattCapacity", "250"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.data.batteryNames").isArray())
                    .andExpect(jsonPath("$.data.batteryNames.length()").value(1))
                    .andExpect(jsonPath("$.data.batteryNames[0]").value("MidWatt"))
                    .andExpect(jsonPath("$.data.totalWattCapacity").value(200))
                    .andExpect(jsonPath("$.data.averageWattCapacity").value(200.0));

            verify(batteryService).getBatteriesByPostcodeRange(1000, 2000, 150L, 250L);
        }

        @Test
        @DisplayName("Should handle invalid watt capacity range")
        void getBatteriesByPostcodeRange_shouldHandleInvalidWattCapacityRange() throws Exception {
            when(batteryService.getBatteriesByPostcodeRange(anyInt(), anyInt(), eq(300L), eq(200L)))
                    .thenThrow(new IllegalArgumentException(ResponseMessages.WATT_CAPACITY_RANGE_INVALID));

            mockMvc.perform(get(BASE_PATH + "/range")
                            .param("from", "1000")
                            .param("to", "2000")
                            .param("minWattCapacity", "300")
                            .param("maxWattCapacity", "200"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value(ResponseMessages.INVALID_ARGUMENT));

            verify(batteryService).getBatteriesByPostcodeRange(1000, 2000, 300L, 200L);
        }

        @Test
        @DisplayName("Should handle negative watt capacity values")
        void getBatteriesByPostcodeRange_shouldHandleNegativeWattCapacity() throws Exception {
            when(batteryService.getBatteriesByPostcodeRange(anyInt(), anyInt(), eq(-100L), isNull()))
                    .thenThrow(new IllegalArgumentException(ResponseMessages.WATT_CAPACITY_MUST_BE_POSITIVE));

            mockMvc.perform(get(BASE_PATH + "/range")
                            .param("from", "1000")
                            .param("to", "2000")
                            .param("minWattCapacity", "-100"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value(ResponseMessages.INVALID_ARGUMENT));

            verify(batteryService).getBatteriesByPostcodeRange(1000, 2000, -100L, null);
        }
    }
}