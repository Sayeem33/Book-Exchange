package com.example.Book_Exchange.service;

import com.example.Book_Exchange.entity.Book;
import com.example.Book_Exchange.entity.SavedBook;
import com.example.Book_Exchange.entity.User;
import com.example.Book_Exchange.repository.SavedBookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SavedBookService {

    private final SavedBookRepository savedBookRepository;

    public SavedBookService(SavedBookRepository savedBookRepository) {
        this.savedBookRepository = savedBookRepository;
    }

    // SAVE BOOK
    public void saveBook(User buyer, Book book) {

        // prevent duplicate save
        if (savedBookRepository.existsByBuyerAndBook(buyer, book)) {
            return;
        }

        SavedBook savedBook = new SavedBook();
        savedBook.setBuyer(buyer);
        savedBook.setBook(book);

        savedBookRepository.save(savedBook);
    }

    // GET ALL SAVED BOOKS
    public List<SavedBook> getSavedBooks(User buyer) {
        return savedBookRepository.findByBuyer(buyer);
    }
}