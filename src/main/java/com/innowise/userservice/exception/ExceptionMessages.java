package com.innowise.userservice.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionMessages {

    public static final String FIELD_VALIDATION_FAILED = "Field validation failed";
    public static final String PARAMETER_TYPE_MISMATCH = "Parameter '%s' must be a '%s'";
    public static final String USER_NOT_FOUND = "User not found";
    public static final String CARD_NOT_FOUND = "Card info not found";

}
