package com.example.Book_Exchange.repository;

import com.example.Book_Exchange.entity.Book;
import com.example.Book_Exchange.entity.SavedBook;
import com.example.Book_Exchange.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavedBookRepository extends JpaRepository<SavedBook, Long> {

    List<SavedBook> findByBuyer(User buyer);

    boolean existsByBuyerAndBook(User buyer, Book book);
}