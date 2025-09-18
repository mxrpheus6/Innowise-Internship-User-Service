package com.innowise.userservice.dao.impl;

import com.innowise.userservice.dao.CardInfoDao;
import com.innowise.userservice.model.CardInfo;
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

@Repository
@RequiredArgsConstructor
public class CardInfoDaoImpl implements CardInfoDao {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<CardInfo> getAll() {
        String sql = "select * from card_info";

        return jdbcTemplate.query(sql, ROW_MAPPER);
    }

    @Override
    public Optional<CardInfo> getCardInfoById(UUID id) {
        CardInfo cardInfo = null;
        String sql = "select * from card_info where id = ?";

        try {
            cardInfo = jdbcTemplate.queryForObject(sql, ROW_MAPPER, id);
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

        String sql = "select * from card_info where id in (:ids)";
        Map<String, Object> params = Map.of("ids", ids);

        return namedParameterJdbcTemplate.query(sql, params, ROW_MAPPER);
    }

    @Override
    public CardInfo createCardInfo(CardInfo cardInfo) {
        String sql = "insert into card_info (number, holder, expiration_date, user_id) values (?, ?, ?, ?) RETURNING id";

        UUID id = jdbcTemplate.queryForObject(
                sql,
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
        String sql = "update card_info set number = ?, holder = ?, expiration_date = ?, user_id = ? where id = ?";

        int updatedRows = jdbcTemplate.update(
                sql,
                cardInfo.getNumber(),
                cardInfo.getHolder(),
                cardInfo.getExpirationDate(),
                cardInfo.getUserId()
        );

        if (updatedRows == 0) {
            throw new EmptyResultDataAccessException("Incorrect result size", 1);
        }

        cardInfo.setId(id);
        return cardInfo;
    }

    @Override
    public void deleteCardInfoById(UUID id) {
        String sql = "delete from card_info where id = ?";
        jdbcTemplate.update(sql, id);
    }
}
