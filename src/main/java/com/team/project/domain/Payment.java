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
public class Payment {

        @Id
        @GeneratedValue
        @Column(name = "payment_id")
        private Long id;

        @ManyToOne
        @JoinColumn(name = "member_id", nullable = false)
        private Member member;

        @Column(nullable = false)
        private int price;

        @Column(nullable = false)
        private String title;


}
