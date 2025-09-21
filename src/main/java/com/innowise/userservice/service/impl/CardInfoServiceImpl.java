package com.innowise.userservice.service.impl;

import com.innowise.userservice.dao.CardInfoDao;
import com.innowise.userservice.dto.request.CardInfoRequest;
import com.innowise.userservice.dto.response.CardInfoResponse;
import com.innowise.userservice.exception.CardInfoNotFoundException;
import com.innowise.userservice.exception.ExceptionMessages;
import com.innowise.userservice.mapper.CardInfoMapper;
import com.innowise.userservice.model.CardInfo;
import com.innowise.userservice.service.CardInfoService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CardInfoServiceImpl implements CardInfoService {

    private final CardInfoDao cardInfoDao;
    private final CardInfoMapper cardInfoMapper;

    @Override
    public List<CardInfoResponse> getAllCardInfos() {
        return cardInfoDao.getAll().stream()
                .map(cardInfoMapper::toResponse)
                .toList();
    }

    @Override
    @Cacheable(value = "card_info", key = "#id")
    public CardInfoResponse getCardInfoById(UUID id) {
        CardInfo cardInfo = cardInfoDao.getCardInfoById(id)
                .orElseThrow(() -> new CardInfoNotFoundException(ExceptionMessages.CARD_NOT_FOUND));

        return cardInfoMapper.toResponse(cardInfo);
    }

    @Override
    public List<CardInfoResponse> getCardInfosByIds(List<UUID> ids) {
        return cardInfoDao.getCardInfosByIds(ids).stream()
                .map(cardInfoMapper::toResponse)
                .toList();
    }

    @Override
    @CachePut(value = "card_info", key = "#result.id")
    public CardInfoResponse createCardInfo(CardInfoRequest cardInfoRequest) {
        CardInfo cardInfo = cardInfoMapper.toEntity(cardInfoRequest);

        cardInfo = cardInfoDao.createCardInfo(cardInfo);

        return cardInfoMapper.toResponse(cardInfo);
    }

    @Override
    @Transactional
    @CachePut(value = "card_info", key = "#id")
    public CardInfoResponse updateCardInfoById(UUID id, CardInfoRequest cardInfoRequest) {
        CardInfo cardInfo = cardInfoMapper.toEntity(cardInfoRequest);

        cardInfo = cardInfoDao.updateCardInfoById(id, cardInfo);

        return cardInfoMapper.toResponse(cardInfo);
    }

    @Override
    @Transactional
    @CacheEvict(value = "card_info", key = "#id")
    public void deleteCardInfoById(UUID id) {
        cardInfoDao.deleteCardInfoById(id);
    }
}
