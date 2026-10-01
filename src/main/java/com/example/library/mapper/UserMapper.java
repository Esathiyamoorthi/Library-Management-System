package com.example.library.mapper;

import com.example.library.dto.UserPatchRequest;
import com.example.library.dto.UserRequest;
import com.example.library.dto.UserResponse;
import com.example.library.entity.User;
import com.example.library.entity.userStatus;
import org.springframework.stereotype.Component;

@Component   // makes it a Spring-managed object we can inject into the service
public class UserMapper {

    // Form -> new database object (used by POST)
    public User toEntity(UserRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setDateOfBirth(request.getDateOfBirth());

        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        } else {
            user.setStatus(userStatus.ACTIVE);      // default when not sent
        }
        return user;
    }

    // Database object -> receipt (used everywhere we reply)
    public UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setAddress(user.getAddress());
        response.setDateOfBirth(user.getDateOfBirth());
        response.setStatus(user.getStatus());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }

    // PUT: replace every field of an existing user
    public void updateEntity(User user, UserRequest request) {
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setDateOfBirth(request.getDateOfBirth());
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
    }

    // PATCH: change only the fields that were sent
    public void patchEntity(User user, UserPatchRequest request) {
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
    }
}