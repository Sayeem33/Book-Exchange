package com.example.Book_Exchange.service;

import com.example.Book_Exchange.dto.book.BookRequest;
import com.example.Book_Exchange.dto.book.BookResponse;
import com.example.Book_Exchange.entity.AppUser;
import com.example.Book_Exchange.entity.Book;
import com.example.Book_Exchange.entity.BookCondition;
import com.example.Book_Exchange.entity.BookStatus;
import com.example.Book_Exchange.exception.ForbiddenOperationException;
import com.example.Book_Exchange.exception.ResourceNotFoundException;
import com.example.Book_Exchange.repository.AppUserRepository;
import com.example.Book_Exchange.repository.BookRepository;
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
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AppUserRepository appUserRepository;

    @InjectMocks
    private BookService bookService;

    private BookRequest request;

    @BeforeEach
    void setUp() {
        request = new BookRequest();
        request.setTitle("Clean Code");
        request.setAuthorName("Robert C. Martin");
        request.setDescription("Software craftsmanship");
        request.setCondition(BookCondition.GOOD);
    }

    @Test
    void shouldCreateBookForSeller() {
        AppUser seller = new AppUser();
        seller.setUsername("seller1");

        when(appUserRepository.findByUsername("seller1")).thenReturn(Optional.of(seller));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> {
            Book book = invocation.getArgument(0);
            book.setStatus(BookStatus.AVAILABLE);
            return book;
        });

        BookResponse response = bookService.createBook(request, "seller1");

        assertEquals("Clean Code", response.getTitle());
        assertEquals("seller1", response.getSellerUsername());
    }

    @Test
    void shouldThrowNotFoundWhenSellerMissing() {
        when(appUserRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.createBook(request, "missing"));
    }

    @Test
    void shouldUpdateBookWhenOwnerMatches() {
        AppUser seller = new AppUser();
        seller.setUsername("seller1");

        Book book = new Book();
        book.setSeller(seller);
        book.setTitle("Old");
        book.setAuthorName("Old Author");
        book.setCondition(BookCondition.FAIR);
        book.setStatus(BookStatus.AVAILABLE);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookResponse response = bookService.updateBook(1L, request, "seller1");

        assertEquals("Clean Code", response.getTitle());
    }

    @Test
    void shouldRejectUpdateWhenOwnerDoesNotMatch() {
        AppUser seller = new AppUser();
        seller.setUsername("seller1");

        Book book = new Book();
        book.setSeller(seller);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        assertThrows(ForbiddenOperationException.class, () -> bookService.updateBook(1L, request, "seller2"));
    }

    @Test
    void shouldDeleteBookWhenOwnerMatches() {
        AppUser seller = new AppUser();
        seller.setUsername("seller1");

        Book book = new Book();
        book.setSeller(seller);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        bookService.deleteBook(1L, "seller1");

        verify(bookRepository).delete(book);
    }
}
