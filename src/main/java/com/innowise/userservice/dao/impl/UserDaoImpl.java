package com.innowise.userservice.dao.impl;

import com.innowise.userservice.dao.UserDao;
import com.innowise.userservice.model.User;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserDaoImpl implements UserDao {

    private static final class SQL {
        static final String GET_ALL = "select * from user_entity";
        static final String GET_BY_ID = "select * from user_entity where id = ?";
        static final String GET_BY_IDS = "select * from user_entity where id in (%s)";
        static final String GET_BY_EMAIL = "select * from user_entity where email = ?";

        // Убрал returning id, так как мы генерим ID сами или берем из объекта
        static final String CREATE_USER = """
            insert into user_entity (id, first_name, last_name, email, username, realm_id, created_timestamp, enabled, email_verified)
            values (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        static final String UPDATE_USER = """
            update user_entity
            set first_name = ?,
                last_name = ?,
                email = ?,
                username = ?,
                enabled = ?
            where id = ?
            """;

        static final String DELETE_USER = "delete from user_entity where id = ?";
    }

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> getAll() {
        return jdbcTemplate.query(SQL.GET_ALL, ROW_MAPPER);
    }

    @Override
    public Optional<User> getUserById(String id) {
        try {
            // Теперь id — это String, передаем напрямую
            User user = jdbcTemplate.queryForObject(SQL.GET_BY_ID, ROW_MAPPER, id);
            return Optional.ofNullable(user);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<User> getUsersByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
        String sql = String.format(SQL.GET_BY_IDS, placeholders);

        return jdbcTemplate.query(sql, ROW_MAPPER, ids.toArray());
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        try {
            User user = jdbcTemplate.queryForObject(SQL.GET_BY_EMAIL, ROW_MAPPER, email);
            return Optional.ofNullable(user);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public User createUser(User user) {
        String newId = (user.getId() != null) ? user.getId() : UUID.randomUUID().toString();
        long timestamp = System.currentTimeMillis();

        String realmId = (user.getRealmId() != null) ? user.getRealmId() : "OAuth";

        jdbcTemplate.update(
                SQL.CREATE_USER,
                newId,
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getUsername(),
                realmId,
                timestamp,
                true,
                false
        );

        user.setId(newId);
        return user;
    }

    @Override
    public User updateUserById(String id, User user) {
        int updatedRows = jdbcTemplate.update(
                SQL.UPDATE_USER,
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getUsername(),
                user.isEnabled(),
                id // String
        );

        if (updatedRows == 0) {
            throw new EmptyResultDataAccessException("Incorrect result size", 1);
        }

        user.setId(id);
        return user;
    }

    @Override
    public void deleteUserById(String id) {
        jdbcTemplate.update(SQL.DELETE_USER, id);
    }
}