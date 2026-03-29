package com.example.Book_Exchange.controller;

import com.example.Book_Exchange.dto.book.BookRequest;
import com.example.Book_Exchange.dto.book.BookResponse;
import com.example.Book_Exchange.service.BookService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping("/seller/books")
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody BookRequest request, Authentication authentication) {
        BookResponse created = bookService.createBook(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/seller/books/{bookId}")
    public ResponseEntity<BookResponse> updateBook(
            @PathVariable Long bookId,
            @Valid @RequestBody BookRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(bookService.updateBook(bookId, request, authentication.getName()));
    }

    @DeleteMapping("/seller/books/{bookId}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long bookId, Authentication authentication) {
        bookService.deleteBook(bookId, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/books/{bookId}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable Long bookId) {
        return ResponseEntity.ok(bookService.getBookById(bookId));
    }

    @GetMapping("/books")
    public ResponseEntity<List<BookResponse>> getAvailableBooks() {
        return ResponseEntity.ok(bookService.getAvailableBooks());
    }

    @GetMapping("/seller/books/mine")
    public ResponseEntity<List<BookResponse>> getMyBooks(Authentication authentication) {
        return ResponseEntity.ok(bookService.getBooksBySeller(authentication.getName()));
    }
}
