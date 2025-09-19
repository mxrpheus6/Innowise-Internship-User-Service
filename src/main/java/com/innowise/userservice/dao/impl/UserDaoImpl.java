package com.innowise.userservice.dao.impl;

import com.innowise.userservice.dao.UserDao;
import com.innowise.userservice.model.User;
import java.sql.Date;
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
        static final String GET_ALL = "select * from users";
        static final String GET_BY_ID = "select * from users where id = ?";
        static final String GET_BY_IDS = "select * from users where id in (%s)";
        static final String GET_BY_EMAIL = "select * from users where email = ?";

        static final String CREATE_USER = """
            insert into users (name, surname, birth_date, email)
            values (?, ?, ?, ?)
            returning id
            """;

        static final String UPDATE_USER = """
            update users
            set name = ?,
                surname = ?,
                birth_date = ?,
                email = ?
            where id = ?
            """;

        static final String DELETE_USER = "delete from users where id = ?";
    }

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> getAll() {
        return jdbcTemplate.query(SQL.GET_ALL, ROW_MAPPER);
    }

    @Override
    public Optional<User> getUserById(UUID id) {
        User user = null;

        try {
            user = jdbcTemplate.queryForObject(SQL.GET_BY_ID, ROW_MAPPER, id);
        } catch (DataAccessException e) {
            return Optional.empty();
        }

        return Optional.ofNullable(user);
    }

    @Override
    public List<User> getUsersByIds(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
        String sql = String.format(SQL.GET_BY_IDS, placeholders);

        return jdbcTemplate.query(sql, ROW_MAPPER, ids.toArray());
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        User user = null;

        try {
            user = jdbcTemplate.queryForObject(SQL.GET_BY_EMAIL, ROW_MAPPER, email);
        } catch (DataAccessException e) {
            return Optional.empty();
        }

        return Optional.ofNullable(user);
    }

    @Override
    public User createUser(User user) {
        UUID id = jdbcTemplate.queryForObject(
                SQL.CREATE_USER,
                UUID.class,
                user.getName(),
                user.getSurname(),
                Date.valueOf(user.getBirthDate()),
                user.getEmail()
        );

        user.setId(id);
        return user;
    }

    @Override
    public User updateUserById(UUID id, User user) {
        int updatedRows = jdbcTemplate.update(
                SQL.UPDATE_USER,
                user.getName(),
                user.getSurname(),
                user.getBirthDate(),
                user.getEmail(),
                id
        );

        if (updatedRows == 0) {
            throw new EmptyResultDataAccessException("Incorrect result size", 1);
        }

        user.setId(id);
        return user;
    }

    @Override
    public void deleteUserById(UUID id) {
        jdbcTemplate.update(SQL.DELETE_USER, id);
    }
}
