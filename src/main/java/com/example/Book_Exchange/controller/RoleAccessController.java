package com.example.Book_Exchange.controller;

import com.example.Book_Exchange.dto.auth.AuthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RoleAccessController {

    @GetMapping("/api/admin/ping")
    public ResponseEntity<AuthResponse> adminPing() {
        return ResponseEntity.ok(new AuthResponse("Admin access granted"));
    }

    @GetMapping("/api/seller/ping")
    public ResponseEntity<AuthResponse> sellerPing() {
        return ResponseEntity.ok(new AuthResponse("Seller access granted"));
    }

    @GetMapping("/api/buyer/ping")
    public ResponseEntity<AuthResponse> buyerPing() {
        return ResponseEntity.ok(new AuthResponse("Buyer access granted"));
    }
}
