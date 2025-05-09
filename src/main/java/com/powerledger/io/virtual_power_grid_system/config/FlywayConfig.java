package com.powerledger.io.virtual_power_grid_system.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {
    private static final Logger LOG = LoggerFactory.getLogger(FlywayConfig.class);

    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return flyway -> {
            LOG.info("Running Flyway migrations");
            flyway.migrate();
            LOG.info("Flyway migrations completed");
        };
    }
}
