package com.team.project.repository;

import com.team.project.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByMemberId(Long memberId);

    Optional<Product> findByIdAndMemberId(Long productId, Long memberId);
}
