package com.innowise.userservice.dao;

import com.innowise.userservice.model.CardInfo;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.RowMapper;

public interface CardInfoDao {

    RowMapper<CardInfo> ROW_MAPPER = (ResultSet resultSet, int rowNum) -> CardInfo.builder()
            .id(UUID.fromString(resultSet.getString("id")))
            .number(resultSet.getString("number"))
            .holder(resultSet.getString("holder"))
            .expirationDate(resultSet.getObject("expiration_date", LocalDate.class))
            .userId(UUID.fromString(resultSet.getString("user_id")))
            .build();

    List<CardInfo> getAll();
    Optional<CardInfo> getCardInfoById(UUID id);
    List<CardInfo> getCardInfosByIds(List<UUID> ids);

    CardInfo createCardInfo(CardInfo cardInfo);

    CardInfo updateCardInfoById(UUID id, CardInfo cardInfo);

    void deleteCardInfoById(UUID id);

}
