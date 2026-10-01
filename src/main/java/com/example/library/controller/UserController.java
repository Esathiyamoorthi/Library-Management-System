package com.example.library.controller;

import com.example.library.dto.UserPatchRequest;
import com.example.library.dto.UserRequest;
import com.example.library.dto.UserResponse;
import com.example.library.service.UserService;
import com.example.library.dto.BookIssueResponse;
import com.example.library.service.BookIssueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.example.library.service.UserService.bookIssueService;

@RestController                  // handles web requests, replies in JSON
@RequestMapping("/api/users")    // every URL in this class starts with /api/users
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;   // the controller only knows the SERVICE

    @PostMapping                            // POST /api/users
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        UserResponse created = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);     // 201
    }

    @GetMapping                              // GET /api/users
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());                // 200
    }

    @GetMapping("/{id}")                     // GET /api/users/5
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")                     // PUT /api/users/5  (replace everything)
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
                                                   @Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @PatchMapping("/{id}")                   // PATCH /api/users/5  (change only some fields)
    public ResponseEntity<UserResponse> patchUser(@PathVariable Long id,
                                                  @Valid @RequestBody UserPatchRequest request) {
        return ResponseEntity.ok(userService.patchUser(id, request));
    }

    @DeleteMapping("/{id}")                  // DELETE /api/users/5
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();                          // 204
    }

    @GetMapping("/{userId}/books")          // GET /api/users/1/books
    public ResponseEntity<List<BookIssueResponse>> getUserBooks(@PathVariable Long userId) {
        return ResponseEntity.ok(bookIssueService.getCurrentIssuesByUser(userId));
    }
}