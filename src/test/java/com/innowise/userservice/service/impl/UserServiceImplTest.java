package com.innowise.userservice.service.impl;

import static com.innowise.userservice.constants.UserTestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innowise.userservice.dao.UserDao;
import com.innowise.userservice.dto.response.UserResponse;
import com.innowise.userservice.exception.UserNotFoundException;
import com.innowise.userservice.mapper.UserMapper;
import com.innowise.userservice.model.User;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserDao userDao;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getAllUsers_ShouldReturnMappedResponses() {
        when(userDao.getAll()).thenReturn(List.of(USER));
        when(userMapper.toResponse(USER)).thenReturn(USER_RESPONSE);

        List<UserResponse> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals(USER_RESPONSE, result.getFirst());
    }

    @Test
    void getUserById_ShouldReturnUser_WhenExists() {
        when(userDao.getUserById(USER_ID)).thenReturn(Optional.of(USER));
        when(userMapper.toResponse(USER)).thenReturn(USER_RESPONSE);

        UserResponse result = userService.getUserById(USER_ID);

        assertEquals(USER_RESPONSE, result);
    }

    @Test
    void getUserById_ShouldThrow_WhenNotFound() {
        when(userDao.getUserById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(USER_ID));
    }

    @Test
    void getUserByEmail_ShouldReturnUser_WhenExists() {
        when(userDao.getUserByEmail(EMAIL)).thenReturn(Optional.of(USER));
        when(userMapper.toResponse(USER)).thenReturn(USER_RESPONSE);

        UserResponse result = userService.getUserByEmail(EMAIL);

        assertEquals(USER_RESPONSE, result);
    }

    @Test
    void getUserByEmail_ShouldThrow_WhenNotFound() {
        when(userDao.getUserByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserByEmail(EMAIL));
    }

    @Test
    void getUsersByIds_ShouldReturnMappedResponses() {
        // ТЕПЕРЬ ЭТО LIST<STRING>
        List<String> ids = List.of(USER_ID);

        when(userDao.getUsersByIds(ids)).thenReturn(List.of(USER));
        when(userMapper.toResponse(USER)).thenReturn(USER_RESPONSE);

        List<UserResponse> responses = userService.getUsersByIds(ids);

        assertEquals(1, responses.size());
        assertEquals(USER_RESPONSE, responses.getFirst());
    }

    @Test
    void createUser_ShouldPersistAndReturnResponse() {
        when(userMapper.toEntity(VALID_USER_REQUEST)).thenReturn(USER);

        when(userDao.createUser(any(User.class))).thenReturn(USER);

        when(userMapper.toResponse(USER)).thenReturn(USER_RESPONSE);

        UserResponse result = userService.createUser(VALID_USER_REQUEST);

        assertEquals(USER_RESPONSE, result);
        verify(userDao).createUser(any(User.class));
    }

    @Test
    void updateUserById_ShouldUpdateAndReturnResponse() {
        when(userMapper.toEntity(UPDATED_USER_REQUEST)).thenReturn(USER);

        when(userDao.updateUserById(eq(USER_ID), any(User.class))).thenReturn(USER);

        when(userMapper.toResponse(USER)).thenReturn(UPDATED_USER_RESPONSE);

        UserResponse result = userService.updateUserById(USER_ID, UPDATED_USER_REQUEST);

        assertEquals(UPDATED_USER_RESPONSE, result);
        verify(userDao).updateUserById(eq(USER_ID), any(User.class));
    }

    @Test
    void deleteUserById_ShouldCallDaoDelete() {
        userService.deleteUserById(USER_ID);

        verify(userDao).deleteUserById(USER_ID);
    }
}