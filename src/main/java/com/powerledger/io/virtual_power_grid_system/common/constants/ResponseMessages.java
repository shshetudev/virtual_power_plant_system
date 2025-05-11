package com.powerledger.io.virtual_power_grid_system.common.constants;

public final class ResponseMessages {
    // Success messages
    public static final String BATTERIES_RETRIEVED_SUCCESS = "Batteries retrieved successfully";
    public static final String BATTERIES_CREATED_SUCCESS = "Batteries created successfully";
    public static final String BATTERIES_REGISTRATION_REQUEST_ACCEPTED = "Battery registration request accepted for processing";

    // Error messages
    public static final String VALIDATION_ERROR = "Validation error";
    public static final String MISSING_REQUIRED_PARAMETER = "Missing required parameter";
    public static final String INVALID_ARGUMENT = "Invalid argument";

    // Parameter validation messages
    public static final String POSTCODE_RANGE_INVALID = "Invalid postcode range: from > to";
    public static final String POSTCODE_MUST_BE_POSITIVE = "Postcode must be a positive number";

    public static final String WATT_CAPACITY_RANGE_INVALID = "Watt capacity range: from > to";
    public static final String WATT_CAPACITY_MUST_BE_POSITIVE = "Watt capacity must be a positive number";

    // Private constructor to prevent instantiation
    private ResponseMessages() {
        throw new AssertionError("Utility class - do not instantiate");
    }
}