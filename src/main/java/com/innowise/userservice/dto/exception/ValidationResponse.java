package com.innowise.userservice.dto.exception;

import java.util.List;

public record ValidationResponse(
        List<Validation> errors
) {
}
