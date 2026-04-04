package com.example.Book_Exchange.controller;

import com.example.Book_Exchange.entity.Book;
import com.example.Book_Exchange.entity.User;
import com.example.Book_Exchange.service.BookService;
import com.example.Book_Exchange.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/seller")
public class SellerController {

    private final BookService bookService;
    private final UserService userService;

    public SellerController(BookService bookService, UserService userService) {
        this.bookService = bookService;
        this.userService = userService;
    }

    // ---------------------------
    // SHOW SELLER BOOK LIST
    // ---------------------------
    @GetMapping("/books")
    public String sellerBooks(Model model, Authentication authentication) {

        String email = authentication.getName();
        User seller = userService.findByEmail(email);

        model.addAttribute("books", bookService.getBooksBySeller(seller));

        return "seller-books";
    }

    // ---------------------------
    // SHOW ADD BOOK FORM
    // ---------------------------
    @GetMapping("/add-book")
    public String addBookForm(Model model) {

        model.addAttribute("book", new Book());

        return "add-book";
    }

    // ---------------------------
    // SAVE BOOK (SELLER ONLY)
    // ---------------------------
    @PostMapping("/add-book")
    public String saveBook(@ModelAttribute Book book,
                           Authentication authentication) {

        String email = authentication.getName();
        User seller = userService.findByEmail(email);

        book.setSeller(seller);

        bookService.saveBook(book);

        return "redirect:/seller/books";
    }
}