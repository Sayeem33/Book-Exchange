package com.example.Book_Exchange.service;

import com.example.Book_Exchange.entity.Book;
import com.example.Book_Exchange.entity.SavedBook;
import com.example.Book_Exchange.entity.User;
import com.example.Book_Exchange.repository.SavedBookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SavedBookServiceTest {

    @Mock
    private SavedBookRepository savedBookRepository;

    @InjectMocks
    private SavedBookService savedBookService;

    // =========================
    // TEST 1: SAVE BOOK SUCCESS
    // =========================
    @Test
    void testSaveBookSuccess() {

        User buyer = new User();
        buyer.setId(1L);

        Book book = new Book();
        book.setId(10L);

        // no duplicate exists
        when(savedBookRepository.existsByBuyerAndBook(buyer, book))
                .thenReturn(false);

        // call service
        savedBookService.saveBook(buyer, book);

        // verify save called
        verify(savedBookRepository, times(1))
                .save(any(SavedBook.class));

        verify(savedBookRepository, times(1))
                .existsByBuyerAndBook(buyer, book);
    }

    // =========================
    // TEST 2: DUPLICATE PREVENTION
    // =========================
    @Test
    void testSaveBookDuplicate() {

        User buyer = new User();
        Book book = new Book();

        when(savedBookRepository.existsByBuyerAndBook(buyer, book))
                .thenReturn(true);

        savedBookService.saveBook(buyer, book);

        // should NOT save
        verify(savedBookRepository, never())
                .save(any(SavedBook.class));

        verify(savedBookRepository, times(1))
                .existsByBuyerAndBook(buyer, book);
    }

    // =========================
    // TEST 3: GET SAVED BOOKS
    // =========================
    @Test
    void testGetSavedBooks() {

        User buyer = new User();
        buyer.setId(1L);

        List<SavedBook> savedBooks = List.of(
                new SavedBook(),
                new SavedBook()
        );

        when(savedBookRepository.findByBuyer(buyer))
                .thenReturn(savedBooks);

        List<SavedBook> result = savedBookService.getSavedBooks(buyer);

        assertEquals(2, result.size());

        verify(savedBookRepository, times(1))
                .findByBuyer(buyer);
    }
}