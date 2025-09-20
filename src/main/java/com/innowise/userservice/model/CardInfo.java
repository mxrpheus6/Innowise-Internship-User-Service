package com.innowise.userservice.model;

import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class CardInfo {
    private UUID id;
    private String number;
    private String holder;
    private LocalDate expirationDate;
    private UUID userId;
}
