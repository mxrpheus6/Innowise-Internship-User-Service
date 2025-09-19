package com.innowise.userservice.dao.impl;

import com.innowise.userservice.dao.CardInfoDao;
import com.innowise.userservice.model.CardInfo;
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
public class CardInfoDaoImpl implements CardInfoDao {

    private static final class SQL {
        static final String GET_ALL = "select * from card_info";
        static final String GET_BY_ID = "select * from card_info where id = ?";
        static final String GET_BY_IDS = "select * from card_info where id in (:ids)";

        static final String CREATE_CARD_INFO = """
            insert into card_info (number, holder, expiration_date, user_id)
            values (?, ?, ?, ?)
            returning id
            """;

        static final String UPDATE_CARD_INFO = """
            update card_info
            set number = ?,
                holder = ?,
                expiration_date = ?,
                user_id = ?
            where id = ?
            """;

        static final String DELETE_CARD_INFO = "delete from card_info where id = ?";
    }

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<CardInfo> getAll() {;
        return jdbcTemplate.query(SQL.GET_ALL, ROW_MAPPER);
    }

    @Override
    public Optional<CardInfo> getCardInfoById(UUID id) {
        CardInfo cardInfo = null;

        try {
            cardInfo = jdbcTemplate.queryForObject(SQL.GET_BY_ID, ROW_MAPPER, id);
        } catch (DataAccessException e) {
            return Optional.empty();
        }

        return Optional.ofNullable(cardInfo);
    }

    @Override
    public List<CardInfo> getCardInfosByIds(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
        String sql = String.format(SQL.GET_BY_IDS, placeholders);

        return jdbcTemplate.query(sql, ROW_MAPPER, ids.toArray());
    }

    @Override
    public CardInfo createCardInfo(CardInfo cardInfo) {
        UUID id = jdbcTemplate.queryForObject(
                SQL.CREATE_CARD_INFO,
                UUID.class,
                cardInfo.getNumber(),
                cardInfo.getHolder(),
                cardInfo.getExpirationDate(),
                cardInfo.getUserId()
        );

        cardInfo.setId(id);
        return cardInfo;
    }

    @Override
    public CardInfo updateCardInfoById(UUID id, CardInfo cardInfo) {
        int updatedRows = jdbcTemplate.update(
                SQL.UPDATE_CARD_INFO,
                cardInfo.getNumber(),
                cardInfo.getHolder(),
                cardInfo.getExpirationDate(),
                cardInfo.getUserId(),
                id
        );

        if (updatedRows == 0) {
            throw new EmptyResultDataAccessException("Incorrect result size", 1);
        }

        cardInfo.setId(id);
        return cardInfo;
    }

    @Override
    public void deleteCardInfoById(UUID id) {
        jdbcTemplate.update(SQL.DELETE_CARD_INFO, id);
    }
}
