package com.example.Book_Exchange.repository;

import com.example.Book_Exchange.entity.ExchangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExchangeRequestRepository extends JpaRepository<ExchangeRequest, Long> {
    List<ExchangeRequest> findByBuyerUsername(String username);

    List<ExchangeRequest> findByBookSellerUsername(String username);
}
