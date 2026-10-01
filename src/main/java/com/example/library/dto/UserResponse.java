package com.example.library.dto;

import com.example.library.entity.userStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private LocalDate dateOfBirth;
    private userStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}