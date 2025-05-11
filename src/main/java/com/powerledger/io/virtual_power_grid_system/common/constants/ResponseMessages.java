package com.powerledger.io.virtual_power_grid_system.common.constants;

public final class ResponseMessages {
    // Success messages
    public static final String BATTERIES_RETRIEVED_SUCCESS = "Batteries retrieved successfully";
    public static final String BATTERIES_CREATED_SUCCESS = "Batteries created successfully";
    
    // Error messages
    public static final String MISSING_PARAMETER = "Parameter is required";
    public static final String VALIDATION_ERROR = "Validation error";
    public static final String CONSTRAINT_VIOLATION = "Constraint violation";
    public static final String MISSING_REQUIRED_PARAMETER = "Missing required parameter";
    public static final String INVALID_ARGUMENT = "Invalid argument";
    public static final String INTERNAL_SERVER_ERROR = "Internal server error";
    public static final String UNEXPECTED_ERROR = "An unexpected error occurred";
    
    // Parameter validation messages
    public static final String POSTCODE_RANGE_INVALID = "Invalid postcode range: from > to";
    public static final String POSTCODE_MUST_BE_POSITIVE = "Postcode must be a positive number";

    // Private constructor to prevent instantiation
    private ResponseMessages() {
        throw new AssertionError("Utility class - do not instantiate");
    }
}