package com.innowise.userservice.dto.response;

import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardInfoResponse {
    private UUID id;
    private String number;
    private String holder;
    private LocalDate expirationDate;
    private UUID userId;
}
