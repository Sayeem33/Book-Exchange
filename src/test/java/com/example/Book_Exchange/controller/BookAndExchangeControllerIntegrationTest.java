package com.example.Book_Exchange.controller;

import com.example.Book_Exchange.entity.AppUser;
import com.example.Book_Exchange.entity.Book;
import com.example.Book_Exchange.entity.BookCondition;
import com.example.Book_Exchange.entity.BookStatus;
import com.example.Book_Exchange.entity.ExchangeRequest;
import com.example.Book_Exchange.entity.ExchangeRequestStatus;
import com.example.Book_Exchange.entity.Role;
import com.example.Book_Exchange.entity.RoleName;
import com.example.Book_Exchange.repository.AppUserRepository;
import com.example.Book_Exchange.repository.BookRepository;
import com.example.Book_Exchange.repository.ExchangeRequestRepository;
import com.example.Book_Exchange.repository.RoleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BookAndExchangeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ExchangeRequestRepository exchangeRequestRepository;

    @BeforeEach
    void resetData() {
        exchangeRequestRepository.deleteAll();
        bookRepository.deleteAll();
        appUserRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "seller1", roles = {"SELLER"})
    void sellerShouldCreateAndDeleteBook() throws Exception {
        saveUserWithRole("seller1", "seller1@example.com", RoleName.SELLER);

        Map<String, Object> payload = new HashMap<>();
        payload.put("title", "Domain-Driven Design");
        payload.put("authorName", "Eric Evans");
        payload.put("description", "DDD fundamentals");
        payload.put("condition", "GOOD");

        String content = objectMapper.writeValueAsString(payload);

        mockMvc.perform(post("/api/seller/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isCreated());

        Book savedBook = bookRepository.findBySellerUsername("seller1").get(0);

        mockMvc.perform(delete("/api/seller/books/{bookId}", savedBook.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "buyer1", roles = {"BUYER"})
    void buyerShouldBeForbiddenFromSellerBookCreateEndpoint() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("title", "Clean Architecture");
        payload.put("authorName", "Robert C. Martin");
        payload.put("description", "Architecture patterns");
        payload.put("condition", "LIKE_NEW");

        mockMvc.perform(post("/api/seller/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "buyer1", roles = {"BUYER"})
    void buyerShouldCreateAndDeleteOwnExchangeRequest() throws Exception {
        AppUser seller = saveUserWithRole("seller1", "seller1@example.com", RoleName.SELLER);
        saveUserWithRole("buyer1", "buyer1@example.com", RoleName.BUYER);

        Book book = new Book();
        book.setTitle("Refactoring");
        book.setAuthorName("Martin Fowler");
        book.setDescription("Refactoring guide");
        book.setCondition(BookCondition.GOOD);
        book.setStatus(BookStatus.AVAILABLE);
        book.setSeller(seller);
        book = bookRepository.save(book);

        Map<String, Object> payload = new HashMap<>();
        payload.put("bookId", book.getId());
        payload.put("message", "I want this book");

        mockMvc.perform(post("/api/buyer/exchange-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated());

        ExchangeRequest request = exchangeRequestRepository.findByBuyerUsername("buyer1").get(0);

        mockMvc.perform(delete("/api/buyer/exchange-requests/{requestId}", request.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "seller1", roles = {"SELLER"})
    void sellerShouldRespondToPendingExchangeRequest() throws Exception {
        AppUser seller = saveUserWithRole("seller1", "seller1@example.com", RoleName.SELLER);
        AppUser buyer = saveUserWithRole("buyer1", "buyer1@example.com", RoleName.BUYER);

        Book book = new Book();
        book.setTitle("Patterns of Enterprise Application Architecture");
        book.setAuthorName("Martin Fowler");
        book.setDescription("POEAA");
        book.setCondition(BookCondition.FAIR);
        book.setStatus(BookStatus.AVAILABLE);
        book.setSeller(seller);
        book = bookRepository.save(book);

        ExchangeRequest request = new ExchangeRequest();
        request.setBook(book);
        request.setBuyer(buyer);
        request.setMessage("Please accept");
        request.setStatus(ExchangeRequestStatus.PENDING);
        request = exchangeRequestRepository.save(request);

        mockMvc.perform(put("/api/seller/exchange-requests/{requestId}/respond", request.getId())
                        .param("accept", "true"))
                .andExpect(status().isOk());
    }

    private AppUser saveUserWithRole(String username, String email, RoleName roleName) {
        Role role = roleRepository.findByName(roleName).orElseGet(() -> roleRepository.save(new Role(roleName)));
        AppUser appUser = new AppUser();
        appUser.setUsername(username);
        appUser.setEmail(email);
        appUser.setPassword("encoded");
        appUser.setRoles(Set.of(role));
        return appUserRepository.save(appUser);
    }
}
