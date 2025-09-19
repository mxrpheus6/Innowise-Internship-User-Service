package com.innowise.userservice.dto.exception;

public record Validation(
        String fieldName,
        String message
) {
}
