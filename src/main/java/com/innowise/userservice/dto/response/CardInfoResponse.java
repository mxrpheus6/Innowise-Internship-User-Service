package com.innowise.userservice.dto.response;

import java.time.LocalDate;
import java.util.UUID;

public record CardInfoResponse(
        UUID id,
        String number,
        String holder,
        LocalDate expirationDate,
        UUID userId
) {}
