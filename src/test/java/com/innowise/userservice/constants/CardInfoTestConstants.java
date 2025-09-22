package com.innowise.userservice.constants;

import com.innowise.userservice.dto.request.CardInfoRequest;
import com.innowise.userservice.dto.response.CardInfoResponse;
import com.innowise.userservice.model.CardInfo;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CardInfoTestConstants {

    public static final UUID CARD_ID = UUID.fromString("00000000-0000-0000-0000-000000000010");
    public static final String CARD_NUMBER = "1234567890123456";
    public static final String CARD_HOLDER = "John Doe";
    public static final LocalDate CARD_EXPIRATION_DATE = LocalDate.of(2030, 12, 31);
    public static final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    public static final String UPDATED_CARD_NUMBER = "9876543210987654";
    public static final String UPDATED_CARD_HOLDER = "Pavel Kazachenko";
    public static final LocalDate UPDATED_EXPIRATION_DATE = LocalDate.of(2035, 6, 30);

    public static final CardInfoRequest VALID_CARD_INFO_REQUEST = new CardInfoRequest(
            CARD_NUMBER,
            CARD_HOLDER,
            CARD_EXPIRATION_DATE,
            USER_ID
    );

    public static final CardInfoRequest UPDATED_CARD_INFO_REQUEST = new CardInfoRequest(
            UPDATED_CARD_NUMBER,
            UPDATED_CARD_HOLDER,
            UPDATED_EXPIRATION_DATE,
            USER_ID
    );

    public static final CardInfoResponse CARD_INFO_RESPONSE = new CardInfoResponse(
            CARD_ID,
            CARD_NUMBER,
            CARD_HOLDER,
            CARD_EXPIRATION_DATE,
            USER_ID
    );

    public static final CardInfoResponse UPDATED_CARD_INFO_RESPONSE = new CardInfoResponse(
            CARD_ID,
            UPDATED_CARD_NUMBER,
            UPDATED_CARD_HOLDER,
            UPDATED_EXPIRATION_DATE,
            USER_ID
    );

    public static final CardInfo CARD_INFO;

    static {
        CARD_INFO = CardInfo.builder()
                .id(CARD_ID)
                .number(CARD_NUMBER)
                .holder(CARD_HOLDER)
                .expirationDate(CARD_EXPIRATION_DATE)
                .userId(USER_ID)
                .build();
    }

}
