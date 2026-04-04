package com.example.Book_Exchange.service;

import com.example.Book_Exchange.entity.Book;
import com.example.Book_Exchange.entity.User;
import com.example.Book_Exchange.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // =========================
    // SELLER FEATURES
    // =========================

    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    public List<Book> getBooksBySeller(User seller) {
        return bookRepository.findBySeller(seller);
    }

    // =========================
    // BUYER FEATURES
    // =========================

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
    }
}