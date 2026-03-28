package com.example.Book_Exchange.repository;

import com.example.Book_Exchange.entity.Role;
import com.example.Book_Exchange.entity.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
