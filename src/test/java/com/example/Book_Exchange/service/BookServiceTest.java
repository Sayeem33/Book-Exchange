package com.example.Book_Exchange.service;

import com.example.Book_Exchange.entity.Book;
import com.example.Book_Exchange.entity.User;
import com.example.Book_Exchange.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    // =========================
    // TEST 1: SAVE BOOK
    // =========================
    @Test
    void testSaveBook() {

        User seller = new User();
        seller.setId(1L);
        seller.setName("Seller");

        Book book = new Book();
        book.setTitle("Java Basics");
        book.setAuthor("John");
        book.setSeller(seller);

        when(bookRepository.save(book)).thenReturn(book);

        Book result = bookService.saveBook(book);

        assertNotNull(result);
        assertEquals("Java Basics", result.getTitle());

        verify(bookRepository, times(1)).save(book);
    }

    // =========================
    // TEST 2: GET ALL BOOKS
    // =========================
    @Test
    void testGetAllBooks() {

        List<Book> books = List.of(
                new Book(1L, "Book A", "Author A", "Desc", null),
                new Book(2L, "Book B", "Author B", "Desc", null)
        );

        when(bookRepository.findAll()).thenReturn(books);

        List<Book> result = bookService.getAllBooks();

        assertEquals(2, result.size());
        assertEquals("Book A", result.get(0).getTitle());

        verify(bookRepository, times(1)).findAll();
    }

    // =========================
    // TEST 3: GET BOOKS BY SELLER
    // =========================
    @Test
    void testGetBooksBySeller() {

        User seller = new User();
        seller.setId(1L);

        List<Book> books = List.of(
                new Book(1L, "Java", "A", "Desc", seller)
        );

        when(bookRepository.findBySeller(seller)).thenReturn(books);

        List<Book> result = bookService.getBooksBySeller(seller);

        assertEquals(1, result.size());
        assertEquals("Java", result.get(0).getTitle());

        verify(bookRepository, times(1)).findBySeller(seller);
    }

    // =========================
    // TEST 4: GET BOOK BY ID (SUCCESS)
    // =========================
    @Test
    void testGetBookByIdSuccess() {

        Book book = new Book(1L, "Spring Boot", "Author", "Desc", null);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Book result = bookService.getBookById(1L);

        assertNotNull(result);
        assertEquals("Spring Boot", result.getTitle());

        verify(bookRepository, times(1)).findById(1L);
    }

    // =========================
    // TEST 5: GET BOOK BY ID (FAILURE)
    // =========================
    @Test
    void testGetBookByIdNotFound() {

        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> bookService.getBookById(1L)
        );

        assertEquals("Book not found with id: 1", exception.getMessage());

        verify(bookRepository, times(1)).findById(1L);
    }
}