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
    @Getter
    @OneToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @Getter
    private long balance;

    @Getter
    private boolean isMainAccount;

    protected Account() {} // JPA 기본 생성자

    public static Account createMainAccount(User user){
        Account account = new Account();
        account.setMainAccount(user, true);
        return account;
    }

    public void setMainAccount(User user, boolean mainAccount) {
        this.user = user;
        isMainAccount = true;
        balance = 0;
    }

}