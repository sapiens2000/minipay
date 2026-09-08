package com.minipay.user;

import com.minipay.account.Account;
import com.minipay.account.SavingsAccount;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "USER")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;

    private String username;
    private String password;
    private Long dailyLimit;

    @Setter
    @Getter
    @OneToOne
    @JoinColumn(name = "ACCOUNT_ID")
    private Account account;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<SavingsAccount> savingsAccount;

    public void setDefault(String username, String password) {
        this.username = username;
        this.password = password;
        this.dailyLimit = 3_000_000L;
    }
}
