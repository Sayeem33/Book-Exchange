package com.example.Book_Exchange.controller;

import com.example.Book_Exchange.repository.BookRepository;
import com.example.Book_Exchange.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AdminControllerTest {

    private MockMvc mockMvc;

    private UserRepository userRepository;
    private BookRepository bookRepository;

    @BeforeEach
    void setup() {

        // MOCK repositories (no Spring, no annotations)
        userRepository = mock(UserRepository.class);
        bookRepository = mock(BookRepository.class);

        // Create controller manually
        AdminController controller =
                new AdminController(userRepository, bookRepository);

        // Build MockMvc manually
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void shouldReturnAdminUsersPage() throws Exception {

        when(userRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-users"))
                .andExpect(model().attributeExists("users"));
    }

    @Test
    void shouldReturnAdminBooksPage() throws Exception {

        when(bookRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/admin/books"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-books"))
                .andExpect(model().attributeExists("books"));
    }
}