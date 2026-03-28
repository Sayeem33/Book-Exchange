package com.example.Book_Exchange.service;

import com.example.Book_Exchange.dto.auth.RegisterRequest;
import com.example.Book_Exchange.entity.AppUser;
import com.example.Book_Exchange.entity.Role;
import com.example.Book_Exchange.entity.RoleName;
import com.example.Book_Exchange.exception.ResourceConflictException;
import com.example.Book_Exchange.repository.AppUserRepository;
import com.example.Book_Exchange.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private RegisterRequest request;

    @BeforeEach
    void setUp() {
        request = new RegisterRequest();
        request.setUsername("alice");
        request.setEmail("alice@example.com");
        request.setPassword("secret123");
    }

    @Test
    void shouldRegisterUserWithEncodedPasswordAndDefaultBuyerRole() {
        Role buyerRole = new Role(RoleName.BUYER);

        when(appUserRepository.existsByUsername("alice")).thenReturn(false);
        when(appUserRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(roleRepository.findByName(RoleName.BUYER)).thenReturn(Optional.of(buyerRole));
        when(passwordEncoder.encode("secret123")).thenReturn("encoded-password");
        when(appUserRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AppUser saved = userService.register(request);

        assertEquals("alice", saved.getUsername());
        assertEquals("alice@example.com", saved.getEmail());
        assertEquals("encoded-password", saved.getPassword());
        assertTrue(saved.getRoles().stream().anyMatch(role -> role.getName() == RoleName.BUYER));
    }

    @Test
    void shouldThrowConflictWhenUsernameAlreadyExists() {
        when(appUserRepository.existsByUsername("alice")).thenReturn(true);

        assertThrows(ResourceConflictException.class, () -> userService.register(request));
    }

    @Test
    void shouldThrowConflictWhenEmailAlreadyExists() {
        when(appUserRepository.existsByUsername("alice")).thenReturn(false);
        when(appUserRepository.existsByEmail("alice@example.com")).thenReturn(true);

        assertThrows(ResourceConflictException.class, () -> userService.register(request));
    }

    @Test
    void shouldUseRequestedSellerRoleWhenProvided() {
        Role sellerRole = new Role(RoleName.SELLER);
        request.setRole("seller");

        when(appUserRepository.existsByUsername("alice")).thenReturn(false);
        when(appUserRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(roleRepository.findByName(RoleName.SELLER)).thenReturn(Optional.of(sellerRole));
        when(passwordEncoder.encode("secret123")).thenReturn("encoded-password");
        when(appUserRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AppUser saved = userService.register(request);

        assertTrue(saved.getRoles().stream().anyMatch(role -> role.getName() == RoleName.SELLER));
    }

    @Test
    void shouldFallbackToBuyerWhenUnknownRoleProvided() {
        Role buyerRole = new Role(RoleName.BUYER);
        request.setRole("random_role");

        when(appUserRepository.existsByUsername("alice")).thenReturn(false);
        when(appUserRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(roleRepository.findByName(RoleName.BUYER)).thenReturn(Optional.of(buyerRole));
        when(passwordEncoder.encode("secret123")).thenReturn("encoded-password");
        when(appUserRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.register(request);

        ArgumentCaptor<AppUser> captor = ArgumentCaptor.forClass(AppUser.class);
        verify(appUserRepository).save(captor.capture());
        assertTrue(captor.getValue().getRoles().stream().anyMatch(role -> role.getName() == RoleName.BUYER));
    }
}
