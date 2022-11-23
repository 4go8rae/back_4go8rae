package com.team.project.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class CartItem {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "cartitem_id")
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "product_id", nullable = false)
        private Product product;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "member_id", nullable = false)
        private Member member;

        @Column
        private Long payment_id;

        @Column(nullable = false)
        private int count;

        @Column(nullable = false)
        private int total;


        public void update(int n) {
                count = n;
                total = count * product.getPrice();
        }

        public void setPayment_id(Long id){
                payment_id = id;
        }
}
