package com.innowise.userservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;

public record CardInfoRequest(

        @NotBlank(message = "{card_info_request.number.blank}")
        @Size(max = 20, message = "{card_info_request.number.size}")
        String number,

        @NotBlank(message = "{card_info_request.holder.blank}")
        @Size(max = 100, message = "{card_info_request.holder.size}")
        String holder,

        @NotNull(message = "{card_info_request.expiration_date.null}")
        LocalDate expirationDate,

        @NotNull(message = "{card_info_request.user_id.null}")
        UUID userId
) {}
