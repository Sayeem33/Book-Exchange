package com.example.Book_Exchange.service;

import com.example.Book_Exchange.entity.User;
import com.example.Book_Exchange.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    // ✅ TEST 1: REGISTER USER
    @Test
    void testRegisterUser() {

        // 1. input user
        User user = new User();
        user.setName("John");
        user.setEmail("john@mail.com");
        user.setPassword("1234");

        // 2. mock password encoder
        when(passwordEncoder.encode("1234")).thenReturn("encoded1234");

        // 3. mock save
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("John");
        savedUser.setEmail("john@mail.com");
        savedUser.setPassword("encoded1234");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // 4. call service
        User result = userService.register(user);

        // 5. assertions
        assertEquals("encoded1234", result.getPassword());
        assertEquals("John", result.getName());

        // 6. verify interactions
        verify(passwordEncoder, times(1)).encode("1234");
        verify(userRepository, times(1)).save(user);
    }

    // ✅ TEST 2: FIND BY EMAIL
    @Test
    void testFindByEmail() {

        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        User result = userService.findByEmail("test@mail.com");

        assertNotNull(result);
        assertEquals("test@mail.com", result.getEmail());

        verify(userRepository, times(1)).findByEmail("test@mail.com");
    }
}