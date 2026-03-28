package com.example.Book_Exchange.mapper;

import com.example.Book_Exchange.dto.book.BookRequest;
import com.example.Book_Exchange.dto.book.BookResponse;
import com.example.Book_Exchange.entity.Book;
import com.example.Book_Exchange.entity.BookStatus;

public final class BookMapper {

    private BookMapper() {
    }

    public static Book fromRequest(BookRequest request) {
        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthorName(request.getAuthorName());
        book.setDescription(request.getDescription());
        book.setCondition(request.getCondition());
        book.setStatus(BookStatus.AVAILABLE);
        return book;
    }

    public static void updateFromRequest(Book book, BookRequest request) {
        book.setTitle(request.getTitle());
        book.setAuthorName(request.getAuthorName());
        book.setDescription(request.getDescription());
        book.setCondition(request.getCondition());
    }

    public static BookResponse toResponse(Book book) {
        BookResponse response = new BookResponse();
        response.setId(book.getId());
        response.setTitle(book.getTitle());
        response.setAuthorName(book.getAuthorName());
        response.setDescription(book.getDescription());
        response.setCondition(book.getCondition());
        response.setStatus(book.getStatus());
        response.setSellerUsername(book.getSeller() != null ? book.getSeller().getUsername() : null);
        return response;
    }
}
