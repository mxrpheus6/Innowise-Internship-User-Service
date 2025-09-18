package com.innowise.userservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record UserRequest(

        @NotBlank(message = "{user_request.name.blank}")
        @Size(max = 100, message = "{user_request.name.size}")
        String name,

        @NotBlank(message = "{user_request.surname.blank}")
        @Size(max = 100, message = "{user_request.surname.size}")
        String surname,

        @NotNull(message = "{user_request.birth_date.null}")
        @Past(message = "{user_request.birth_date.past}")
        LocalDate birthDate,

        @Email(message = "{user_request.email.invalid}")
        @NotBlank(message = "{user_request.email.blank}")
        @Size(max = 255, message = "{user_request.email.size}")
        String email
) {}
