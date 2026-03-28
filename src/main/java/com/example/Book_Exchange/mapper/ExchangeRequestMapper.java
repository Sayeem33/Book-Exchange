package com.example.Book_Exchange.mapper;

import com.example.Book_Exchange.dto.exchange.ExchangeRequestResponse;
import com.example.Book_Exchange.entity.ExchangeRequest;

public final class ExchangeRequestMapper {

    private ExchangeRequestMapper() {
    }

    public static ExchangeRequestResponse toResponse(ExchangeRequest exchangeRequest) {
        ExchangeRequestResponse response = new ExchangeRequestResponse();
        response.setId(exchangeRequest.getId());
        response.setBookId(exchangeRequest.getBook().getId());
        response.setBuyerUsername(exchangeRequest.getBuyer().getUsername());
        response.setSellerUsername(exchangeRequest.getBook().getSeller().getUsername());
        response.setMessage(exchangeRequest.getMessage());
        response.setStatus(exchangeRequest.getStatus());
        response.setCreatedAt(exchangeRequest.getCreatedAt());
        return response;
    }
}
