package com.example.Book_Exchange.config;

import com.example.Book_Exchange.entity.Role;
import com.example.Book_Exchange.entity.RoleName;
import com.example.Book_Exchange.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleDataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public RoleDataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        seedRole(RoleName.ADMIN);
        seedRole(RoleName.SELLER);
        seedRole(RoleName.BUYER);
    }

    private void seedRole(RoleName roleName) {
        roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(new Role(roleName)));
    }
}
