package com.example.webjpa.repositories.db2;

import com.example.webjpa.entities.db1.User;
import com.example.webjpa.entities.db2.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductionRepository extends JpaRepository<Product, Long> {
    Optional<Product> findById(Long id);
}
