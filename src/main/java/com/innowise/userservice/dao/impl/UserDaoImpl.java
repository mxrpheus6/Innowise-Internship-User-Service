package com.innowise.userservice.dao.impl;

import com.innowise.userservice.dao.UserDao;
import com.innowise.userservice.model.User;
import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class UserDaoImpl implements UserDao {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<User> getAll() {
        String sql = "select * from users";

        return jdbcTemplate.query(sql, ROW_MAPPER);
    }

    @Override
    public Optional<User> getUserById(UUID id) {
        User user = null;
        String sql = "select * from users where id = ?";

        try {
            user = jdbcTemplate.queryForObject(sql, ROW_MAPPER, id);
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

        String sql = "select * from users where id in (:ids)";
        Map<String, Object> params = Map.of("ids", ids);

        return namedParameterJdbcTemplate.query(sql, params, ROW_MAPPER);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        User user = null;

        String sql = "select * from users where email = ?";

        try {
            user = jdbcTemplate.queryForObject(sql, ROW_MAPPER, email);
        } catch (DataAccessException e) {
            return Optional.empty();
        }

        return Optional.ofNullable(user);
    }

    @Override
    public User createUser(User user) {
        String sql = "insert into users (name, surname, birth_date, email) values (?, ?, ?, ?) RETURNING id";

        UUID id = jdbcTemplate.queryForObject(
                sql,
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
        String sql = "update users set name = ?, surname = ?, birth_date = ?, email = ? where id = ?";

        int updatedRows = jdbcTemplate.update(
                sql,
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
    @Transactional
    public void deleteUserById(UUID id) {
        String deleteCardInfo = "delete from card_info where user_id = ?";
        jdbcTemplate.update(deleteCardInfo, id);

        String deleteUser = "delete from users where id = ?";
        jdbcTemplate.update(deleteUser, id);
    }
}
