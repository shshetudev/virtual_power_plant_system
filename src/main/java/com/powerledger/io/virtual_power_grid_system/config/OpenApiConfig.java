package com.powerledger.io.virtual_power_grid_system.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Virtual Power Grid System API")
                        .version("1.0")
                        .description("API documentation for the Virtual Power Grid System")
                        .contact(new Contact()
                                .name("PowerLedger")
                                .email("support@powerledger.io")));
    }
}