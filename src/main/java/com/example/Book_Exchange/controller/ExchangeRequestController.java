package com.example.Book_Exchange.controller;

import com.example.Book_Exchange.dto.exchange.ExchangeRequestCreateRequest;
import com.example.Book_Exchange.dto.exchange.ExchangeRequestResponse;
import com.example.Book_Exchange.service.ExchangeRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ExchangeRequestController {

    private final ExchangeRequestService exchangeRequestService;

    public ExchangeRequestController(ExchangeRequestService exchangeRequestService) {
        this.exchangeRequestService = exchangeRequestService;
    }

    @PostMapping("/buyer/exchange-requests")
    public ResponseEntity<ExchangeRequestResponse> createExchangeRequest(
            @Valid @RequestBody ExchangeRequestCreateRequest request,
            Authentication authentication
    ) {
        ExchangeRequestResponse response = exchangeRequestService.createRequest(
                request.getBookId(),
                authentication.getName(),
                request.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/seller/exchange-requests/{requestId}/respond")
    public ResponseEntity<ExchangeRequestResponse> respondToRequest(
            @PathVariable Long requestId,
            @RequestParam boolean accept,
            Authentication authentication
    ) {
        return ResponseEntity.ok(exchangeRequestService.respondToRequest(requestId, authentication.getName(), accept));
    }

    @GetMapping("/exchange-requests/{requestId}")
    public ResponseEntity<ExchangeRequestResponse> getRequestById(
            @PathVariable Long requestId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(exchangeRequestService.getRequestById(requestId, authentication.getName()));
    }

    @GetMapping("/buyer/exchange-requests/mine")
    public ResponseEntity<List<ExchangeRequestResponse>> getBuyerRequests(Authentication authentication) {
        return ResponseEntity.ok(exchangeRequestService.getRequestsForBuyer(authentication.getName()));
    }

    @GetMapping("/seller/exchange-requests/incoming")
    public ResponseEntity<List<ExchangeRequestResponse>> getSellerRequests(Authentication authentication) {
        return ResponseEntity.ok(exchangeRequestService.getRequestsForSeller(authentication.getName()));
    }

    @DeleteMapping("/buyer/exchange-requests/{requestId}")
    public ResponseEntity<Void> deleteRequest(@PathVariable Long requestId, Authentication authentication) {
        exchangeRequestService.deleteRequest(requestId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
