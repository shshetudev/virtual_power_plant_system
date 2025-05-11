package com.powerledger.io.virtual_power_grid_system.common.dto;

import java.time.LocalDateTime;

public class Response<T> {
    private LocalDateTime timestamp;
    private int status;
    private String message;
    private T data;

    private Response(int status, String message, T data) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> Response<T> success(int status, String message, T data) {
        return new Response<>(status, message, data);
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}