package com.example.Book_Exchange.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PageControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void homeShouldBePublic() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));
    }

    @Test
    void loginPageShouldBePublic() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    @WithMockUser(username = "seller", roles = {"SELLER"})
    void sellerShouldAccessSellerDashboard() throws Exception {
        mockMvc.perform(get("/seller/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("seller-dashboard"));
    }

    @Test
    @WithMockUser(username = "buyer", roles = {"BUYER"})
    void buyerShouldBeForbiddenFromSellerDashboard() throws Exception {
        mockMvc.perform(get("/seller/dashboard"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "buyer", roles = {"BUYER"})
    void buyerShouldAccessBuyerRequestsPage() throws Exception {
        mockMvc.perform(get("/buyer/requests"))
                .andExpect(status().isOk())
                .andExpect(view().name("buyer-requests"));
    }

    @Test
    @WithMockUser(username = "seller", roles = {"SELLER"})
    void sellerShouldBeForbiddenFromBuyerRequestsPage() throws Exception {
        mockMvc.perform(get("/buyer/requests"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void adminShouldAccessAdminPanel() throws Exception {
        mockMvc.perform(get("/admin/panel"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-panel"));
    }

    @Test
    @WithMockUser(username = "buyer", roles = {"BUYER"})
    void buyerShouldBeForbiddenFromAdminPanel() throws Exception {
        mockMvc.perform(get("/admin/panel"))
                .andExpect(status().isForbidden());
    }
}
