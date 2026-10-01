package com.example.library.service;

import com.example.library.dto.*;
import com.example.library.serviceimpl.BookIssueServiceImpl;
import lombok.Getter;
import lombok.Setter;
import org.apache.coyote.Response;

import java.util.List;



// A list of promises: "any UserService must be able to do these things."
public interface UserService {

    final BookIssueService bookIssueService = null;



    UserResponse createUser(UserRequest request);

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Long id);

    UserResponse updateUser(Long id, UserRequest request);      // PUT

    UserResponse patchUser(Long id, UserPatchRequest request);  // PATCH

    void deleteUser(Long id);
}