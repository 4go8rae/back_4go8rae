package com.team.project.repository;

import com.team.project.domain.Member;
import com.team.project.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findAllByMember(Member member);
}
