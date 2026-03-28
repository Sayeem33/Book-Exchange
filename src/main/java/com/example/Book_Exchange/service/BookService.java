package com.example.Book_Exchange.service;

import com.example.Book_Exchange.dto.book.BookRequest;
import com.example.Book_Exchange.dto.book.BookResponse;
import com.example.Book_Exchange.entity.AppUser;
import com.example.Book_Exchange.entity.Book;
import com.example.Book_Exchange.entity.BookStatus;
import com.example.Book_Exchange.exception.ForbiddenOperationException;
import com.example.Book_Exchange.exception.ResourceNotFoundException;
import com.example.Book_Exchange.mapper.BookMapper;
import com.example.Book_Exchange.repository.AppUserRepository;
import com.example.Book_Exchange.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final AppUserRepository appUserRepository;

    public BookService(BookRepository bookRepository, AppUserRepository appUserRepository) {
        this.bookRepository = bookRepository;
        this.appUserRepository = appUserRepository;
    }

    public BookResponse createBook(BookRequest request, String sellerUsername) {
        AppUser seller = appUserRepository.findByUsername(sellerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found: " + sellerUsername));

        Book book = BookMapper.fromRequest(request);
        book.setSeller(seller);
        return BookMapper.toResponse(bookRepository.save(book));
    }

    public BookResponse updateBook(Long bookId, BookRequest request, String sellerUsername) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + bookId));

        if (!book.getSeller().getUsername().equals(sellerUsername)) {
            throw new ForbiddenOperationException("Only the owner can update this book");
        }

        BookMapper.updateFromRequest(book, request);
        return BookMapper.toResponse(bookRepository.save(book));
    }

    public void deleteBook(Long bookId, String sellerUsername) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + bookId));

        if (!book.getSeller().getUsername().equals(sellerUsername)) {
            throw new ForbiddenOperationException("Only the owner can delete this book");
        }

        bookRepository.delete(book);
    }

    public BookResponse getBookById(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + bookId));
        return BookMapper.toResponse(book);
    }

    public List<BookResponse> getAvailableBooks() {
        return bookRepository.findByStatus(BookStatus.AVAILABLE)
                .stream()
                .map(BookMapper::toResponse)
                .toList();
    }

    public List<BookResponse> getBooksBySeller(String sellerUsername) {
        return bookRepository.findBySellerUsername(sellerUsername)
                .stream()
                .map(BookMapper::toResponse)
                .toList();
    }
}
