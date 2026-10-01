package com.example.library.dto;


import com.example.library.entity.userStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserRequest {

        @NotBlank(message = "Name is required")
        private String name;

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        private String email;

        @NotBlank(message = "Phone is required")
        @Pattern(regexp = "\\d{10}", message = "Phone must be exactly 10 digits")
        private String phone;

        private String address;                      // optional

        @PastOrPresent(message = "Date of birth cannot be in the future")
        private LocalDate dateOfBirth;               // optional

        private userStatus status;                   // optional, defaults to ACTIVE later
}