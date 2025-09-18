package com.innowise.userservice.mapper;

import com.innowise.userservice.dto.request.CardInfoRequest;
import com.innowise.userservice.dto.response.CardInfoResponse;
import com.innowise.userservice.model.CardInfo;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface CardInfoMapper {

    CardInfoResponse toResponse(CardInfo cardInfo);
    CardInfo toEntity(CardInfoRequest cardInfoRequest);

}
