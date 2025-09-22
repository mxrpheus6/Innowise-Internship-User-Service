package com.innowise.userservice.service.impl;

import com.innowise.userservice.dao.UserDao;
import com.innowise.userservice.dto.request.UserRequest;
import com.innowise.userservice.dto.response.UserResponse;
import com.innowise.userservice.exception.ExceptionMessages;
import com.innowise.userservice.exception.UserNotFoundException;
import com.innowise.userservice.mapper.UserMapper;
import com.innowise.userservice.model.User;
import com.innowise.userservice.service.UserService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final UserMapper userMapper;

    @Override
    public List<UserResponse> getAllUsers() {
        return userDao.getAll().stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Override
    public UserResponse getUserById(UUID id) {
        User user = userDao.getUserById(id)
                .orElseThrow(() -> new UserNotFoundException(ExceptionMessages.USER_NOT_FOUND));

        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        User user = userDao.getUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(ExceptionMessages.USER_NOT_FOUND));

        return userMapper.toResponse(user);
    }

    @Override
    public List<UserResponse> getUsersByIds(List<UUID> ids) {
        return userDao.getUsersByIds(ids).stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Override
    public UserResponse createUser(UserRequest userRequest) {
        User user = userMapper.toEntity(userRequest);

        user = userDao.createUser(user);

        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUserById(UUID id, UserRequest userRequest) {
        User user = userMapper.toEntity(userRequest);

        user = userDao.updateUserById(id, user);

        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public void deleteUserById(UUID id) {
        userDao.deleteUserById(id);
    }
}
