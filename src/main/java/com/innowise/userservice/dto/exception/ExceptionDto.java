package com.innowise.userservice.dto.exception;

import java.time.LocalDateTime;

public record ExceptionDto(
        LocalDateTime timestamp,
        String message
) {}
