package com.example.library.dto;

import com.example.library.entity.userStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserPatchRequest {

    // Everything is optional here. But IF a value is sent, it must be valid.

    @Pattern(regexp = ".*\\S.*", message = "Name cannot be blank")
    private String name;

    @Email(message = "Email must be valid")
    private String email;

    @Pattern(regexp = "\\d{10}", message = "Phone must be exactly 10 digits")
    private String phone;

    private String address;

    @PastOrPresent(message = "Date of birth cannot be in the future")
    private LocalDate dateOfBirth;

    private userStatus status;
}