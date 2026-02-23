package com.innowise.userservice.service;

import com.innowise.userservice.dto.request.UserRequest;
import com.innowise.userservice.dto.response.UserResponse;
import java.util.List;

public interface UserService {
    List<UserResponse> getAllUsers();

    UserResponse getUserById(String id);

    UserResponse getUserByEmail(String email);

    List<UserResponse> getUsersByIds(List<String> ids);

    UserResponse createUser(UserRequest userRequest);

    UserResponse updateUserById(String id, UserRequest userRequest);

    void deleteUserById(String id);
}