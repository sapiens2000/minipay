package com.minipay.account;

import com.minipay.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ACCOUNT")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Column(name = "ACCOUNT_ID")
    private Long id;

    @Setter
    @OneToOne(mappedBy = "account")
    private User user;

    @Getter
    private long amount;

    public User getUser() {
        return user;
    }

    protected Account() {} // JPA 기본 생성자

    public Account(long amount){
        this.amount = amount;
    }

    public void withdraw(long amount){
        this.amount -= amount;
    }

    public void deposit(long amount){
        this.amount += amount;
    }

}