package com.team.project.repository;

import com.team.project.domain.ChatRoom;
import com.team.project.domain.Member;
import com.team.project.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByCustomer_IdAndSeller_IdAndProduct_Id(Long customerId, Long sellerId, Long productId);
    @Query("SELECT r FROM ChatRoom r WHERE r.customer.id = :memberId or r.seller.id = :memberId")
    List<ChatRoom> findAllByMemberId(Long memberId);

    @Query(value = "SELECT r FROM ChatRoom r JOIN FETCH r.customer, r.seller, r.product")
    ChatRoom findByCustomerAndSellerAndProduct(Member customer, Member seller, Product product);

}
