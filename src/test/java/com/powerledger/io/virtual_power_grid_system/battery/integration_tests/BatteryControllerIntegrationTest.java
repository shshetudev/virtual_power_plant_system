package com.powerledger.io.virtual_power_grid_system.battery.integration_tests;

import com.powerledger.io.virtual_power_grid_system.battery.dto.BatteryRangeResponseDto;
import com.powerledger.io.virtual_power_grid_system.battery.dto.BatteryRequestDto;
import com.powerledger.io.virtual_power_grid_system.battery.model.Battery;
import com.powerledger.io.virtual_power_grid_system.battery.queue.BatteryRegistrationProducer;
import com.powerledger.io.virtual_power_grid_system.battery.repository.BatteryRepository;
import com.powerledger.io.virtual_power_grid_system.common.constants.ResponseMessages;
import com.powerledger.io.virtual_power_grid_system.common.dto.Response;
import com.powerledger.io.virtual_power_grid_system.common.integration_tests.BaseRepositoryIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.anyList;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BatteryControllerIntegrationTest extends BaseRepositoryIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private BatteryRepository batteryRepository;

    @MockitoBean
    private BatteryRegistrationProducer batteryRegistrationProducer;

    private String baseUrl;

    @BeforeEach
    void setup() {
        batteryRepository.deleteAll();
        baseUrl = "/api/v1/batteries";
    }

    @Test
    @DisplayName("POST /batteries - Should accept battery registration request and return 202 Accepted")
    void saveBatteries_shouldAcceptRequestAndDelegateToProducer() {
        List<BatteryRequestDto> batteryRequests = Arrays.asList(
                new BatteryRequestDto("Battery Alpha", 6000, 13500L),
                new BatteryRequestDto("Battery Beta", 6001, 15000L)
        );

        HttpEntity<List<BatteryRequestDto>> requestEntity = new HttpEntity<>(batteryRequests);

        ResponseEntity<Response<Void>> responseEntity = restTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().getStatus()).isEqualTo(HttpStatus.ACCEPTED.value());
        assertThat(responseEntity.getBody().getMessage()).isEqualTo(ResponseMessages.BATTERIES_REGISTRATION_REQUEST_ACCEPTED);

        verify(batteryRegistrationProducer).enqueueBatteryRegistrations(anyList());
    }

    @Test
    @DisplayName("GET /batteries/range - Should retrieve batteries within postcode range")
    void getBatteriesByPostcodeRange_shouldReturnFilteredBatteries() {
        batteryRepository.saveAll(Arrays.asList(
                new Battery("Battery Gamma", 7000, 2000L),
                new Battery("Battery Delta", 7050, 3000L),
                new Battery("Battery Epsilon", 7100, 1000L),
                new Battery("Battery Zeta", 6999, 500L)
        ));

        String url = baseUrl + "/range?from=7000&to=7050";

        ResponseEntity<Response<BatteryRangeResponseDto>> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(responseEntity.getBody().getMessage()).isEqualTo(ResponseMessages.BATTERIES_RETRIEVED_SUCCESS);

        BatteryRangeResponseDto rangeResponse = responseEntity.getBody().getData();
        assertThat(rangeResponse).isNotNull();
        assertThat(rangeResponse.getBatteryNames()).isNotNull();
        assertThat(rangeResponse.getBatteryNames()).hasSize(2);
        assertThat(rangeResponse.getTotalWattCapacity()).isEqualTo(5000L);
    }

    @Test
    @DisplayName("GET /batteries/range - Should return empty list for no batteries in range")
    void getBatteriesByPostcodeRange_shouldReturnEmptyListForNoMatches() {
        batteryRepository.saveAll(Arrays.asList(
                new Battery("Battery Outlier", 8000, 2000L)
        ));

        String url = baseUrl + "/range?from=7000&to=7050";

        ResponseEntity<Response<BatteryRangeResponseDto>> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().getData().getBatteryNames()).isEmpty();
        assertThat(responseEntity.getBody().getData().getTotalWattCapacity()).isZero();
    }

    @Test
    @DisplayName("GET /batteries/range - Should handle invalid range (from > to) and return bad request")
    void getBatteriesByPostcodeRange_whenInvalidRange_shouldReturnBadRequest() {
        String url = baseUrl + "/range?from=7050&to=7000";

        ResponseEntity<Response<Object>> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(responseEntity.getBody().getData()).isNull();
    }

    @Test
    @DisplayName("GET /batteries/range - Should return bad request if 'from' postcode is missing")
    void getBatteriesByPostcodeRange_whenFromPostcodeMissing_shouldReturnBadRequest() {
        String url = baseUrl + "/range?to=7000"; // 'from' is missing

        ResponseEntity<Response<Object>> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Response<Object>>() {}
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("GET /batteries/range - Should return bad request for invalid watt capacity range (min > max)")
    void getBatteriesByPostcodeRange_whenInvalidWattCapacityRange_shouldReturnBadRequest() {
        String url = baseUrl + "/range?from=7000&to=7050&minWattCapacity=1000&maxWattCapacity=500";

        ResponseEntity<Response<Object>> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().getData()).isNull();
    }
}