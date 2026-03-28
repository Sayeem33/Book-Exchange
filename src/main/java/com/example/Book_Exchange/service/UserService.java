package com.example.Book_Exchange.service;

import com.example.Book_Exchange.dto.auth.RegisterRequest;
import com.example.Book_Exchange.entity.AppUser;
import com.example.Book_Exchange.entity.Role;
import com.example.Book_Exchange.entity.RoleName;
import com.example.Book_Exchange.exception.ResourceConflictException;
import com.example.Book_Exchange.repository.AppUserRepository;
import com.example.Book_Exchange.repository.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Set;

@Service
public class UserService {

    private final AppUserRepository appUserRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(AppUserRepository appUserRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AppUser register(RegisterRequest request) {
        if (appUserRepository.existsByUsername(request.getUsername())) {
            throw new ResourceConflictException("Username already exists");
        }
        if (appUserRepository.existsByEmail(request.getEmail())) {
            throw new ResourceConflictException("Email already exists");
        }

        RoleName roleName = parseRole(request.getRole());
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalStateException("Role not initialized: " + roleName));

        AppUser appUser = new AppUser();
        appUser.setUsername(request.getUsername());
        appUser.setEmail(request.getEmail());
        appUser.setPassword(passwordEncoder.encode(request.getPassword()));
        appUser.setRoles(Set.of(role));

        return appUserRepository.save(appUser);
    }

    private RoleName parseRole(String roleValue) {
        if (roleValue == null || roleValue.isBlank()) {
            return RoleName.BUYER;
        }

        try {
            return RoleName.valueOf(roleValue.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return RoleName.BUYER;
        }
    }
}
