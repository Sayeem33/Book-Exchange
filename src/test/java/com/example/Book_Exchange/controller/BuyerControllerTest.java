package com.example.Book_Exchange.controller;

import com.example.Book_Exchange.entity.Book;
import com.example.Book_Exchange.entity.SavedBook;
import com.example.Book_Exchange.entity.User;
import com.example.Book_Exchange.service.BookService;
import com.example.Book_Exchange.service.SavedBookService;
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

class BuyerControllerTest {

    private MockMvc mockMvc;

    private BookService bookService;
    private UserService userService;
    private SavedBookService savedBookService;
    private Authentication authentication;

    @BeforeEach
    void setup() {

        bookService = mock(BookService.class);
        userService = mock(UserService.class);
        savedBookService = mock(SavedBookService.class);
        authentication = mock(Authentication.class);

        BuyerController controller =
                new BuyerController(bookService, userService, savedBookService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    // ------------------------------------
    // 1. BROWSE BOOKS
    // ------------------------------------
    @Test
    void shouldReturnBuyerBooksPage() throws Exception {

        when(bookService.getAllBooks()).thenReturn(List.of(
                new Book(),
                new Book()
        ));

        mockMvc.perform(get("/buyer/books"))
                .andExpect(status().isOk())
                .andExpect(view().name("buyer-books"))
                .andExpect(model().attributeExists("books"));
    }

    // ------------------------------------
    // 2. BOOK DETAILS (AJAX)
    // ------------------------------------
    @Test
    void shouldReturnBookDetails() throws Exception {

        Book book = new Book();
        book.setId(1L);

        when(bookService.getBookById(1L)).thenReturn(book);

        mockMvc.perform(get("/buyer/book/1"))
                .andExpect(status().isOk());
    }

    // ------------------------------------
    // 3. SAVE BOOK
    // ------------------------------------
    @Test
    void shouldSaveBookAndRedirect() throws Exception {

        when(authentication.getName()).thenReturn("test@email.com");

        User user = new User();
        when(userService.findByEmail("test@email.com")).thenReturn(user);

        Book book = new Book();
        when(bookService.getBookById(1L)).thenReturn(book);

        doNothing().when(savedBookService).saveBook(user, book);

        mockMvc.perform(post("/buyer/save-book/1")
                        .principal(authentication))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/buyer/books"));

        verify(savedBookService, times(1))
                .saveBook(user, book);
    }

}