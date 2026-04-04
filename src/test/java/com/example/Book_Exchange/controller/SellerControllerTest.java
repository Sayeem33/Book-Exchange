package com.example.Book_Exchange.controller;

import com.example.Book_Exchange.entity.Book;
import com.example.Book_Exchange.entity.User;
import com.example.Book_Exchange.service.BookService;
import com.example.Book_Exchange.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SellerControllerTest {

    private MockMvc mockMvc;

    private BookService bookService;
    private UserService userService;
    private Authentication authentication;

    @BeforeEach
    void setup() {

        bookService = mock(BookService.class);
        userService = mock(UserService.class);
        authentication = mock(Authentication.class);

        SellerController controller =
                new SellerController(bookService, userService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    // -----------------------------------
    // 1. SELLER BOOK LIST
    // -----------------------------------
    @Test
    void shouldReturnSellerBooksPage() throws Exception {

        when(authentication.getName()).thenReturn("seller@test.com");

        User seller = new User();
        when(userService.findByEmail("seller@test.com")).thenReturn(seller);

        when(bookService.getBooksBySeller(seller))
                .thenReturn(List.of(new Book(), new Book()));

        mockMvc.perform(get("/seller/books")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(view().name("seller-books"))
                .andExpect(model().attributeExists("books"));
    }


    // -----------------------------------
    // 2. SAVE BOOK
    // -----------------------------------
    @Test
    void shouldSaveBookAndRedirect() throws Exception {

        when(authentication.getName()).thenReturn("seller@test.com");

        User seller = new User();
        when(userService.findByEmail("seller@test.com")).thenReturn(seller);

        mockMvc.perform(post("/seller/add-book")
                        .param("title", "Spring Boot")
                        .param("author", "John")
                        .principal(authentication))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/seller/books"));

        verify(bookService, times(1)).saveBook(any(Book.class));
    }
}