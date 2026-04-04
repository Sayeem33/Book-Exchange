package com.example.Book_Exchange.controller;

import com.example.Book_Exchange.repository.BookRepository;
import com.example.Book_Exchange.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public AdminController(UserRepository userRepository,
                           BookRepository bookRepository) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    // 👑 View all users
    @GetMapping("/users")
    public String allUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin-users";
    }

    // 📚 View all books
    @GetMapping("/books")
    public String allBooks(Model model) {
        model.addAttribute("books", bookRepository.findAll());
        return "admin-books";
    }
}