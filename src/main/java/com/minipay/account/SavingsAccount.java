package com.minipay.account;

import com.minipay.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "SAVINGS_ACCOUNT")
public class SavingsAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="SAVINGS_ACCOUNT_ID")
    @Getter
    private Long id;

    @Getter
    private long balance;

    @Setter
    @Getter
    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @Getter
    @Setter
    private double RateOfInterest;

    protected SavingsAccount() {} // JPA 기본 생성자

    public static SavingsAccount createSavingsAccount(User user){
        SavingsAccount savings = new SavingsAccount();
        savings.setUser(user);
        savings.setRateOfInterest(0.0);
        return savings;
    }

    public void deposit(long amount){
        this.balance += amount;
    }

}
