package com.innowise.userservice.controller;

import com.innowise.userservice.dto.request.UserRequest;
import com.innowise.userservice.dto.response.UserResponse;
import com.innowise.userservice.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        UserResponse user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/batch")
    public ResponseEntity<List<UserResponse>> getUsersByIds(@RequestParam List<UUID> ids) {
        List<UserResponse> users = userService.getUsersByIds(ids);
        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody @Valid UserRequest userRequest) {
        UserResponse createdUser = userService.createUser(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUserById(
            @PathVariable UUID id,
            @RequestBody @Valid UserRequest userRequest
    ) {
        UserResponse updatedUser = userService.updateUserById(id, userRequest);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable UUID id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

}
