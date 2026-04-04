package com.example.Book_Exchange.service;

import com.example.Book_Exchange.entity.User;
import com.example.Book_Exchange.entity.Role;
import com.example.Book_Exchange.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    // =========================
    // TEST 1: USER FOUND
    // =========================
    @Test
    void testLoadUserByUsernameSuccess() {

        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");
        user.setPassword("encodedpass");
        user.setRole(Role.SELLER);

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        UserDetails userDetails =
                customUserDetailsService.loadUserByUsername("test@mail.com");

        assertNotNull(userDetails);
        assertEquals("test@mail.com", userDetails.getUsername());
        assertEquals("encodedpass", userDetails.getPassword());
        assertTrue(
                userDetails.getAuthorities()
                        .stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_SELLER"))
        );

        verify(userRepository, times(1))
                .findByEmail("test@mail.com");
    }

    // =========================
    // TEST 2: USER NOT FOUND
    // =========================
    @Test
    void testLoadUserByUsernameNotFound() {

        when(userRepository.findByEmail("missing@mail.com"))
                .thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("missing@mail.com")
        );

        assertEquals("User not found", exception.getMessage());

        verify(userRepository, times(1))
                .findByEmail("missing@mail.com");
    }
}