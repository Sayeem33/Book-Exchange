package com.example.Book_Exchange.repository;

import com.example.Book_Exchange.entity.Book;
import com.example.Book_Exchange.entity.BookStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findBySellerUsername(String username);

    List<Book> findByStatus(BookStatus status);
}
