package com.innowise.userservice.service;

import com.innowise.userservice.dto.request.CardInfoRequest;
import com.innowise.userservice.dto.response.CardInfoResponse;
import java.util.List;
import java.util.UUID;

public interface CardInfoService {

    List<CardInfoResponse> getAllCardInfos();

    CardInfoResponse getCardInfoById(UUID id);

    List<CardInfoResponse> getCardInfosByIds(List<UUID> ids);

    CardInfoResponse createCardInfo(CardInfoRequest cardInfoRequest);

    CardInfoResponse updateCardInfoById(UUID id, CardInfoRequest cardInfoRequest);

    void deleteCardInfoById(UUID id);

}
