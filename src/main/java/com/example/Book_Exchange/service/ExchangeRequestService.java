package com.example.Book_Exchange.service;

import com.example.Book_Exchange.dto.exchange.ExchangeRequestResponse;
import com.example.Book_Exchange.entity.AppUser;
import com.example.Book_Exchange.entity.Book;
import com.example.Book_Exchange.entity.BookStatus;
import com.example.Book_Exchange.entity.ExchangeRequest;
import com.example.Book_Exchange.entity.ExchangeRequestStatus;
import com.example.Book_Exchange.exception.BusinessValidationException;
import com.example.Book_Exchange.exception.ForbiddenOperationException;
import com.example.Book_Exchange.exception.ResourceNotFoundException;
import com.example.Book_Exchange.mapper.ExchangeRequestMapper;
import com.example.Book_Exchange.repository.AppUserRepository;
import com.example.Book_Exchange.repository.BookRepository;
import com.example.Book_Exchange.repository.ExchangeRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ExchangeRequestService {

    private final ExchangeRequestRepository exchangeRequestRepository;
    private final BookRepository bookRepository;
    private final AppUserRepository appUserRepository;

    public ExchangeRequestService(
            ExchangeRequestRepository exchangeRequestRepository,
            BookRepository bookRepository,
            AppUserRepository appUserRepository
    ) {
        this.exchangeRequestRepository = exchangeRequestRepository;
        this.bookRepository = bookRepository;
        this.appUserRepository = appUserRepository;
    }

    public ExchangeRequestResponse createRequest(Long bookId, String buyerUsername, String message) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + bookId));

        AppUser buyer = appUserRepository.findByUsername(buyerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Buyer not found: " + buyerUsername));

        if (book.getStatus() != BookStatus.AVAILABLE) {
            throw new BusinessValidationException("Book is not available for exchange");
        }

        if (book.getSeller().getUsername().equals(buyerUsername)) {
            throw new BusinessValidationException("Seller cannot request exchange for own book");
        }

        ExchangeRequest exchangeRequest = new ExchangeRequest();
        exchangeRequest.setBook(book);
        exchangeRequest.setBuyer(buyer);
        exchangeRequest.setMessage(message);
        exchangeRequest.setStatus(ExchangeRequestStatus.PENDING);

        return ExchangeRequestMapper.toResponse(exchangeRequestRepository.save(exchangeRequest));
    }

    public ExchangeRequestResponse respondToRequest(Long requestId, String sellerUsername, boolean accept) {
        ExchangeRequest exchangeRequest = exchangeRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Exchange request not found: " + requestId));

        if (!exchangeRequest.getBook().getSeller().getUsername().equals(sellerUsername)) {
            throw new ForbiddenOperationException("Only seller can respond to exchange request");
        }

        if (exchangeRequest.getStatus() != ExchangeRequestStatus.PENDING) {
            throw new BusinessValidationException("Only pending requests can be updated");
        }

        if (accept) {
            exchangeRequest.setStatus(ExchangeRequestStatus.ACCEPTED);
            Book book = exchangeRequest.getBook();
            book.setStatus(BookStatus.EXCHANGED);
            bookRepository.save(book);
        } else {
            exchangeRequest.setStatus(ExchangeRequestStatus.REJECTED);
        }

        return ExchangeRequestMapper.toResponse(exchangeRequestRepository.save(exchangeRequest));
    }

    public ExchangeRequestResponse getRequestById(Long requestId, String username) {
        ExchangeRequest exchangeRequest = exchangeRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Exchange request not found: " + requestId));

        boolean isBuyer = exchangeRequest.getBuyer().getUsername().equals(username);
        boolean isSeller = exchangeRequest.getBook().getSeller().getUsername().equals(username);
        if (!isBuyer && !isSeller) {
            throw new ForbiddenOperationException("Access denied for this exchange request");
        }

        return ExchangeRequestMapper.toResponse(exchangeRequest);
    }

    public void deleteRequest(Long requestId, String buyerUsername) {
        ExchangeRequest exchangeRequest = exchangeRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Exchange request not found: " + requestId));

        if (!exchangeRequest.getBuyer().getUsername().equals(buyerUsername)) {
            throw new ForbiddenOperationException("Only buyer can delete this exchange request");
        }

        if (exchangeRequest.getStatus() != ExchangeRequestStatus.PENDING) {
            throw new BusinessValidationException("Only pending requests can be deleted");
        }

        exchangeRequestRepository.delete(exchangeRequest);
    }

    public List<ExchangeRequestResponse> getRequestsForBuyer(String buyerUsername) {
        return exchangeRequestRepository.findByBuyerUsername(buyerUsername)
                .stream()
                .map(ExchangeRequestMapper::toResponse)
                .toList();
    }

    public List<ExchangeRequestResponse> getRequestsForSeller(String sellerUsername) {
        return exchangeRequestRepository.findByBookSellerUsername(sellerUsername)
                .stream()
                .map(ExchangeRequestMapper::toResponse)
                .toList();
    }
}
