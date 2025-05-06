package com.authenthication.auth_session.Repository;

import com.authenthication.auth_session.Entity.Products;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@EnableJpaRepositories
@Repository
public interface ProductsRepo extends JpaRepository<Products, Long> {
    Optional<Products> findByName(String name);  // Changed from findByProductName to findByName
}

