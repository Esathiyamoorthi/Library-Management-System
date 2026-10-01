package com.example.library.serviceimpl;

import com.example.library.dto.UserPatchRequest;
import com.example.library.exception.ResourceInUseException;
import com.example.library.dto.UserRequest;
import com.example.library.dto.UserResponse;
import com.example.library.entity.User;
import com.example.library.exception.DuplicateResourceException;
import com.example.library.exception.UserNotFoundException;
import com.example.library.mapper.UserMapper;
import com.example.library.repository.BookIssueRepository;
import com.example.library.repository.UserRepository;
import com.example.library.service.BookIssueService;
import com.example.library.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service                  // "this is a business-logic class, manage it for me"
@RequiredArgsConstructor  // Lombok writes a constructor for the final fields below
@Transactional            // each method is one all-or-nothing database transaction
public class UserServiceImpl implements UserService {

    // Spring hands these to us automatically (dependency injection)
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final BookIssueRepository bookIssueRepository;



    @Override
    public UserResponse createUser(UserRequest request) {
        // Rule: no two users may share an email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists: " + request.getEmail());
        }

        User user = userMapper.toEntity(request);
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();

        List<UserResponse> responses = new ArrayList<>();
        for (User user : users) {
            responses.add(userMapper.toResponse(user));
        }
        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = findUserOrThrow(id);
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest request) {
        User user = findUserOrThrow(id);

        // Rule: the email may not belong to a DIFFERENT user
        if (userRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new DuplicateResourceException("Email already exists: " + request.getEmail());
        }

        userMapper.updateEntity(user, request);
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse patchUser(Long id, UserPatchRequest request) {
        User user = findUserOrThrow(id);

        // Only check the email if the client actually sent one
        if (request.getEmail() != null
                && userRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new DuplicateResourceException("Email already exists: " + request.getEmail());
        }

        userMapper.patchEntity(user, request);
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Override
    public void deleteUser(Long id) {
        User user = findUserOrThrow(id);

        // Rule: a user with borrowing history cannot be deleted
        if (bookIssueRepository.existsByUserId(id)) {
            throw new ResourceInUseException("User " + id
                    + " has borrowing records and cannot be deleted. "
                    + "Set the status to INACTIVE instead.");
        }

        userRepository.delete(user);
    }

    // Helper: find the user or throw a 404. Written once, used everywhere.
    private User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }


}