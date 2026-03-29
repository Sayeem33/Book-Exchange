package com.example.Book_Exchange.controller;

import com.example.Book_Exchange.service.BookService;
import com.example.Book_Exchange.service.ExchangeRequestService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    private final BookService bookService;
    private final ExchangeRequestService exchangeRequestService;

    public PageController(BookService bookService, ExchangeRequestService exchangeRequestService) {
        this.bookService = bookService;
        this.exchangeRequestService = exchangeRequestService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("books", bookService.getAvailableBooks());
        return "home";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @GetMapping("/seller/dashboard")
    public String sellerDashboard(Authentication authentication, Model model) {
        model.addAttribute("books", bookService.getBooksBySeller(authentication.getName()));
        return "seller-dashboard";
    }

    @GetMapping("/buyer/requests")
    public String buyerRequests(Authentication authentication, Model model) {
        model.addAttribute("requests", exchangeRequestService.getRequestsForBuyer(authentication.getName()));
        return "buyer-requests";
    }

    @GetMapping("/admin/panel")
    public String adminPanel() {
        return "admin-panel";
    }
}
