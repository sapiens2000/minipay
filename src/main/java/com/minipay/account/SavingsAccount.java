package com.minipay.account;

import com.minipay.user.User;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "SAVINGS_ACCOUNT")
public class SavingsAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="SAVINGS_ACCOUNT_ID")
    private Long id;

    @Getter
    private long amount;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    protected SavingsAccount() {} // JPA 기본 생성자

}
