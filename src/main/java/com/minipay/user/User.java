package com.minipay.user;

import com.minipay.account.Account;
import com.minipay.account.SavingsAccount;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.List;

@Entity
@Table(name="USER", uniqueConstraints = {
        @UniqueConstraint(
                name="USERNAME_UNIQUE",
                columnNames={"USERNAME"}
        )})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    @Getter
    private Long id;

    @Getter
    private String username;

    @Getter
    private String password;

    @ColumnDefault("3000000")
    @Getter
    private Long dailyLimit;

    @Getter
    @ColumnDefault("0")
    private Long used;

    @Setter
    @Getter
    @OneToOne(mappedBy = "user")
    private Account account;

    @Getter
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<SavingsAccount> savingsAccount;

    public void setDefault(String username, String password) {
        this.username = username;
        this.password = password;
        this.dailyLimit = 3_000_000L;
        this.used = 0L;
    }
}
