package com.innowise.userservice.dao;

import com.innowise.userservice.model.User;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.RowMapper;

public interface UserDao {

    RowMapper<User> ROW_MAPPER = (ResultSet resultSet, int rowNum) -> User.builder()
            .id(UUID.fromString(resultSet.getString("id")))
            .name(resultSet.getString("name"))
            .surname(resultSet.getString("surname"))
            .birthDate(resultSet.getObject("birth_date", LocalDate.class))
            .email(resultSet.getString("email"))
            .build();

    List<User> getAll();
    Optional<User> getUserById(UUID id);
    List<User> getUsersByIds(List<UUID> ids);
    Optional<User> getUserByEmail(String email);

    User createUser(User user);

    User updateUserById(UUID id, User user);

    void deleteUserById(UUID id);
}
