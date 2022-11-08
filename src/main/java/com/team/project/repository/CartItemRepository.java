package com.team.project.repository;

import com.team.project.domain.CartItem;
import com.team.project.domain.Member;
import com.team.project.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findAllByMember(Member member);
    void deleteAllByProduct(Product product);
    Optional<CartItem> findByMemberAndProduct(Member member, Product product);
}
