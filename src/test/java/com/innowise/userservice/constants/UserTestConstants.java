package com.innowise.userservice.constants;

import com.innowise.userservice.dto.request.UserRequest;
import com.innowise.userservice.dto.response.UserResponse;
import com.innowise.userservice.model.User;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserTestConstants {

    // 1. UUID заменен на String
    public static final String USER_ID = "00000000-0000-0000-0000-000000000010";

    public static final String NAME = "John";
    public static final String SURNAME = "Doe";
    public static final String USERNAME = "john_doe_user";
    public static final LocalDate BIRTH_DATE = LocalDate.of(1995, 5, 20);
    public static final String EMAIL = "john.doe@example.com";
    public static final String REALM_ID = "OAuth";

    public static final String UPDATED_NAME = "Pavel";
    public static final String UPDATED_SURNAME = "Kazachenko";
    public static final String UPDATED_USERNAME = "pavel_k";
    public static final LocalDate UPDATED_BIRTH_DATE = LocalDate.of(2004, 9, 12);
    public static final String UPDATED_EMAIL = "pavel.kazachenko@example.com";

    public static final UserRequest VALID_USER_REQUEST = new UserRequest(
            NAME,
            SURNAME,
            BIRTH_DATE,
            EMAIL
    );

    public static final UserRequest UPDATED_USER_REQUEST = new UserRequest(
            UPDATED_NAME,
            UPDATED_SURNAME,
            UPDATED_BIRTH_DATE,
            UPDATED_EMAIL
    );

    public static final UserResponse USER_RESPONSE = new UserResponse(
            USER_ID,
            NAME,
            SURNAME,
            EMAIL
    );

    public static final UserResponse UPDATED_USER_RESPONSE = new UserResponse(
            USER_ID,
            UPDATED_NAME,
            UPDATED_SURNAME,
            UPDATED_EMAIL
    );

    public static final User USER;

    static {
        USER = User.builder()
                .id(USER_ID)
                .firstName(NAME)
                .lastName(SURNAME)
                .username(USERNAME)
                .email(EMAIL)
                .emailVerified(false)
                .enabled(true)
                .realmId(REALM_ID)
                .createdTimestamp(System.currentTimeMillis())
                .build();
    }
}