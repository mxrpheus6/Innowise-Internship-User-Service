package com.innowise.userservice.service.impl;

import static com.innowise.userservice.constants.CardInfoTestConstants.CARD_ID;
import static com.innowise.userservice.constants.CardInfoTestConstants.CARD_INFO;
import static com.innowise.userservice.constants.CardInfoTestConstants.CARD_INFO_RESPONSE;
import static com.innowise.userservice.constants.CardInfoTestConstants.UPDATED_CARD_INFO_REQUEST;
import static com.innowise.userservice.constants.CardInfoTestConstants.UPDATED_CARD_INFO_RESPONSE;
import static com.innowise.userservice.constants.CardInfoTestConstants.VALID_CARD_INFO_REQUEST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innowise.userservice.dao.CardInfoDao;
import com.innowise.userservice.dto.response.CardInfoResponse;
import com.innowise.userservice.exception.CardInfoNotFoundException;
import com.innowise.userservice.mapper.CardInfoMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CardInfoServiceImplTest {

    @Mock
    private CardInfoDao cardInfoDao;

    @Mock
    private CardInfoMapper cardInfoMapper;

    @InjectMocks
    private CardInfoServiceImpl cardInfoService;

    @Test
    void getAllCardInfos_ShouldReturnMappedResponses() {
        when(cardInfoDao.getAll()).thenReturn(List.of(CARD_INFO));
        when(cardInfoMapper.toResponse(CARD_INFO)).thenReturn(CARD_INFO_RESPONSE);

        List<CardInfoResponse> result = cardInfoService.getAllCardInfos();

        assertEquals(1, result.size());
        assertEquals(CARD_INFO_RESPONSE, result.get(0));
    }

    @Test
    void getCardInfoById_ShouldReturnCard_WhenExists() {
        when(cardInfoDao.getCardInfoById(CARD_ID)).thenReturn(Optional.of(CARD_INFO));
        when(cardInfoMapper.toResponse(CARD_INFO)).thenReturn(CARD_INFO_RESPONSE);

        CardInfoResponse result = cardInfoService.getCardInfoById(CARD_ID);

        assertEquals(CARD_INFO_RESPONSE, result);
    }

    @Test
    void getCardInfoById_ShouldThrow_WhenNotFound() {
        when(cardInfoDao.getCardInfoById(CARD_ID)).thenReturn(Optional.empty());

        assertThrows(CardInfoNotFoundException.class, () -> cardInfoService.getCardInfoById(CARD_ID));
    }

    @Test
    void getCardInfosByIds_ShouldReturnMappedResponses() {
        List<UUID> ids = List.of(CARD_ID);
        when(cardInfoDao.getCardInfosByIds(ids)).thenReturn(List.of(CARD_INFO));
        when(cardInfoMapper.toResponse(CARD_INFO)).thenReturn(CARD_INFO_RESPONSE);

        List<CardInfoResponse> result = cardInfoService.getCardInfosByIds(ids);

        assertEquals(1, result.size());
        assertEquals(CARD_INFO_RESPONSE, result.get(0));
    }

    @Test
    void createCardInfo_ShouldPersistAndReturnResponse() {
        when(cardInfoMapper.toEntity(VALID_CARD_INFO_REQUEST)).thenReturn(CARD_INFO);
        when(cardInfoDao.createCardInfo(CARD_INFO)).thenReturn(CARD_INFO);
        when(cardInfoMapper.toResponse(CARD_INFO)).thenReturn(CARD_INFO_RESPONSE);

        CardInfoResponse result = cardInfoService.createCardInfo(VALID_CARD_INFO_REQUEST);

        assertEquals(CARD_INFO_RESPONSE, result);
        verify(cardInfoDao).createCardInfo(CARD_INFO);
    }

    @Test
    void updateCardInfoById_ShouldUpdateAndReturnResponse() {
        when(cardInfoMapper.toEntity(UPDATED_CARD_INFO_REQUEST)).thenReturn(CARD_INFO);
        when(cardInfoDao.updateCardInfoById(CARD_ID, CARD_INFO)).thenReturn(CARD_INFO);
        when(cardInfoMapper.toResponse(CARD_INFO)).thenReturn(UPDATED_CARD_INFO_RESPONSE);

        CardInfoResponse result = cardInfoService.updateCardInfoById(CARD_ID, UPDATED_CARD_INFO_REQUEST);

        assertEquals(UPDATED_CARD_INFO_RESPONSE, result);
        verify(cardInfoDao).updateCardInfoById(CARD_ID, CARD_INFO);
    }

    @Test
    void deleteCardInfoById_ShouldCallDaoDelete() {
        cardInfoService.deleteCardInfoById(CARD_ID);

        verify(cardInfoDao).deleteCardInfoById(CARD_ID);
    }
}
