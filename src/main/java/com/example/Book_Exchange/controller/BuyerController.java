package com.example.Book_Exchange.controller;

import com.example.Book_Exchange.entity.Book;
import com.example.Book_Exchange.entity.SavedBook;
import com.example.Book_Exchange.entity.User;
import com.example.Book_Exchange.service.BookService;
import com.example.Book_Exchange.service.SavedBookService;
import com.example.Book_Exchange.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/buyer")
public class BuyerController {

    private final BookService bookService;
    private final UserService userService;
    private final SavedBookService savedBookService;

    public BuyerController(BookService bookService,
                           UserService userService,
                           SavedBookService savedBookService) {
        this.bookService = bookService;
        this.userService = userService;
        this.savedBookService = savedBookService;
    }

    // ------------------------------------
    // 1. BROWSE ALL BOOKS
    // ------------------------------------
    @GetMapping("/books")
    public String browseBooks(Model model) {

        List<Book> books = bookService.getAllBooks();

        model.addAttribute("books", books);

        return "buyer-books";
    }

    // ------------------------------------
    // 2. BOOK DETAILS (POPUP AJAX)
    // ------------------------------------
    @GetMapping("/book/{id}")
    @ResponseBody
    public Book getBookDetails(@PathVariable Long id) {

        return bookService.getBookById(id);
    }

    // ------------------------------------
    // 3. SAVE BOOK (REAL DB IMPLEMENTATION)
    // ------------------------------------
    @PostMapping("/save-book/{id}")
    public String saveBook(@PathVariable Long id,
                           Authentication authentication) {

        String email = authentication.getName();
        User buyer = userService.findByEmail(email);

        Book book = bookService.getBookById(id);

        savedBookService.saveBook(buyer, book);

        return "redirect:/buyer/books";
    }

    // ------------------------------------
    // 4. VIEW SAVED BOOKS (REAL DATA)
    // ------------------------------------
    @GetMapping("/saved-books")
    public String savedBooks(Model model,
                             Authentication authentication) {

        String email = authentication.getName();
        User buyer = userService.findByEmail(email);

        List<SavedBook> savedBooks = savedBookService.getSavedBooks(buyer);

        model.addAttribute("savedBooks", savedBooks);

        return "saved-books";
    }
}