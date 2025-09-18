package com.innowise.userservice.service;

import com.innowise.userservice.dto.request.UserRequest;
import com.innowise.userservice.dto.response.UserResponse;
import java.util.List;
import java.util.UUID;

public interface UserService {

    List<UserResponse> getAllUsers();

    UserResponse getUserById(UUID id);

    UserResponse getUserByEmail(String email);

    List<UserResponse> getUsersByIds(List<UUID> ids);

    UserResponse createUser(UserRequest userRequest);

    UserResponse updateUserById(UUID id, UserRequest userRequest);

    void deleteUserById(UUID id);

}
