package com.example.Book_Exchange.service;

import com.example.Book_Exchange.dto.exchange.ExchangeRequestResponse;
import com.example.Book_Exchange.entity.AppUser;
import com.example.Book_Exchange.entity.Book;
import com.example.Book_Exchange.entity.BookStatus;
import com.example.Book_Exchange.entity.ExchangeRequest;
import com.example.Book_Exchange.entity.ExchangeRequestStatus;
import com.example.Book_Exchange.exception.BusinessValidationException;
import com.example.Book_Exchange.exception.ForbiddenOperationException;
import com.example.Book_Exchange.repository.AppUserRepository;
import com.example.Book_Exchange.repository.BookRepository;
import com.example.Book_Exchange.repository.ExchangeRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExchangeRequestServiceTest {

    @Mock
    private ExchangeRequestRepository exchangeRequestRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AppUserRepository appUserRepository;

    @InjectMocks
    private ExchangeRequestService exchangeRequestService;

    private AppUser seller;
    private AppUser buyer;
    private Book book;

    @BeforeEach
    void setUp() {
        seller = new AppUser();
        seller.setUsername("seller1");

        buyer = new AppUser();
        buyer.setUsername("buyer1");

        book = new Book();
        book.setSeller(seller);
        book.setStatus(BookStatus.AVAILABLE);
    }

    @Test
    void shouldCreateExchangeRequestForAvailableBook() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(appUserRepository.findByUsername("buyer1")).thenReturn(Optional.of(buyer));
        when(exchangeRequestRepository.save(any(ExchangeRequest.class))).thenAnswer(invocation -> {
            ExchangeRequest request = invocation.getArgument(0);
            request.setStatus(ExchangeRequestStatus.PENDING);
            request.setBook(book);
            request.setBuyer(buyer);
            return request;
        });

        ExchangeRequestResponse response = exchangeRequestService.createRequest(1L, "buyer1", "Want to exchange");

        assertEquals(ExchangeRequestStatus.PENDING, response.getStatus());
        assertEquals("buyer1", response.getBuyerUsername());
    }

    @Test
    void shouldRejectRequestWhenBuyerIsSeller() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(appUserRepository.findByUsername("seller1")).thenReturn(Optional.of(seller));

        assertThrows(BusinessValidationException.class,
                () -> exchangeRequestService.createRequest(1L, "seller1", "self request"));
    }

    @Test
    void shouldAcceptPendingRequestWhenSellerResponds() {
        ExchangeRequest request = new ExchangeRequest();
        request.setBook(book);
        request.setBuyer(buyer);
        request.setStatus(ExchangeRequestStatus.PENDING);

        when(exchangeRequestRepository.findById(10L)).thenReturn(Optional.of(request));
        when(exchangeRequestRepository.save(any(ExchangeRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ExchangeRequestResponse response = exchangeRequestService.respondToRequest(10L, "seller1", true);

        assertEquals(ExchangeRequestStatus.ACCEPTED, response.getStatus());
        assertEquals(BookStatus.EXCHANGED, book.getStatus());
        verify(bookRepository).save(book);
    }

    @Test
    void shouldRejectResponseWhenNonSellerTriesToRespond() {
        ExchangeRequest request = new ExchangeRequest();
        request.setBook(book);
        request.setBuyer(buyer);
        request.setStatus(ExchangeRequestStatus.PENDING);

        when(exchangeRequestRepository.findById(10L)).thenReturn(Optional.of(request));

        assertThrows(ForbiddenOperationException.class,
                () -> exchangeRequestService.respondToRequest(10L, "otherSeller", true));
    }

    @Test
    void shouldRejectWhenRequestIsNotPending() {
        ExchangeRequest request = new ExchangeRequest();
        request.setBook(book);
        request.setBuyer(buyer);
        request.setStatus(ExchangeRequestStatus.REJECTED);

        when(exchangeRequestRepository.findById(10L)).thenReturn(Optional.of(request));

        assertThrows(BusinessValidationException.class,
                () -> exchangeRequestService.respondToRequest(10L, "seller1", true));
    }
}
