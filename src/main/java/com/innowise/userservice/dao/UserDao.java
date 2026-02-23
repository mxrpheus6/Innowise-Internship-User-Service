package com.innowise.userservice.dao;

import com.innowise.userservice.model.User;
import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.RowMapper;

public interface UserDao {

    RowMapper<User> ROW_MAPPER = (ResultSet resultSet, int rowNum) -> User.builder()
            .id(resultSet.getString("id"))
            .email(resultSet.getString("email"))
            .emailConstraint(resultSet.getString("email_constraint"))
            .emailVerified(resultSet.getBoolean("email_verified"))
            .enabled(resultSet.getBoolean("enabled"))
            .federationLink(resultSet.getString("federation_link"))
            .firstName(resultSet.getString("first_name"))
            .lastName(resultSet.getString("last_name"))
            .realmId(resultSet.getString("realm_id"))
            .username(resultSet.getString("username"))
            .createdTimestamp(resultSet.getLong("created_timestamp"))
            .serviceAccountClientLink(resultSet.getString("service_account_client_link"))
            .notBefore(resultSet.getInt("not_before"))
            .build();

    List<User> getAll();
    Optional<User> getUserById(String id);
    List<User> getUsersByIds(List<String> ids);
    Optional<User> getUserByEmail(String email);

    User createUser(User user);
    User updateUserById(String id, User user);
    void deleteUserById(String id);
}