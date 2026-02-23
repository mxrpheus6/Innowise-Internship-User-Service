package com.innowise.userservice.controller;

import com.innowise.userservice.dto.request.CardInfoRequest;
import com.innowise.userservice.dto.response.CardInfoResponse;
import com.innowise.userservice.service.CardInfoService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/card-infos")
@RequiredArgsConstructor
public class CardInfoController {

    private final CardInfoService cardInfoService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CardInfoResponse>> getAllCardInfos() {
        List<CardInfoResponse> cards = cardInfoService.getAllCardInfos();
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardInfoResponse> getCardInfoById(@PathVariable UUID id) {
        CardInfoResponse cardInfo = cardInfoService.getCardInfoById(id);
        return ResponseEntity.ok(cardInfo);
    }

    @GetMapping("/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CardInfoResponse>> getCardInfosByIds(@RequestParam List<UUID> ids) {
        List<CardInfoResponse> cards = cardInfoService.getCardInfosByIds(ids);
        return ResponseEntity.ok(cards);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardInfoResponse> createCardInfo(@RequestBody @Valid CardInfoRequest cardInfoRequest) {
        CardInfoResponse createdCardInfo = cardInfoService.createCardInfo(cardInfoRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCardInfo);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardInfoResponse> updateCardInfoById(
            @PathVariable UUID id,
            @RequestBody @Valid CardInfoRequest cardInfoRequest
    ) {
        CardInfoResponse updatedCardInfo = cardInfoService.updateCardInfoById(id, cardInfoRequest);
        return ResponseEntity.ok(updatedCardInfo);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCardInfoById(@PathVariable UUID id) {
        cardInfoService.deleteCardInfoById(id);
        return ResponseEntity.noContent().build();
    }

}
