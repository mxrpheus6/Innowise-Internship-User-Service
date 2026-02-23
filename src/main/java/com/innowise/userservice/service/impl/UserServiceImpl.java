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
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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
    @Cacheable(value = "user", key = "#id")
    public UserResponse getUserById(String id) { // UUID -> String
        User user = userDao.getUserById(id)
                .orElseThrow(() -> new UserNotFoundException(id)); // .toString() больше не нужен

        return userMapper.toResponse(user);
    }

    @Override
    @Cacheable(value = "user", key = "#email")
    public UserResponse getUserByEmail(String email) {
        User user = userDao.getUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(ExceptionMessages.USER_NOT_FOUND));

        return userMapper.toResponse(user);
    }

    @Override
    public List<UserResponse> getUsersByIds(List<String> ids) { // List<UUID> -> List<String>
        return userDao.getUsersByIds(ids).stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    @CachePut(value = "user", key = "#result.id")
    public UserResponse createUser(UserRequest userRequest) {
        User user = userMapper.toEntity(userRequest);

        user = userDao.createUser(user);

        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    @CachePut(value = "user", key = "#id")
    public UserResponse updateUserById(String id, UserRequest userRequest) { // UUID -> String
        User user = userMapper.toEntity(userRequest);

        user = userDao.updateUserById(id, user);

        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    @CacheEvict(value = "user", key = "#id")
    public void deleteUserById(String id) { // UUID -> String
        userDao.deleteUserById(id);
    }
}