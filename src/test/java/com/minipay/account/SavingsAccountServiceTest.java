package com.minipay.account;

import com.minipay.CleanUp;
import com.minipay.user.User;
import com.minipay.user.UserCreateRequest;
import com.minipay.user.UserRepository;
import com.minipay.user.UserService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SavingsAccountServiceTest {

    @Autowired
    private SavingsAccountService savingsAccountService;

    @Autowired
    private SavingsAccountRepository savingsAccountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CleanUp cleanUp;

    @Autowired
    private AccountRepository accountRepository;

    @AfterEach
    void tearDown(){
        cleanUp.all();
    }


    @Test
    void createDefaultSavingsAccount() {
        User user = new User();
        user.setDefault("default", "default");
        user = userRepository.save(user);

        savingsAccountService.createSavingsAccount(user);


        List<SavingsAccount> list = savingsAccountRepository.findByUserIdOrderByIdAsc(user.getId());
        // 가장 마지막에 추가된 계좌 조회
        SavingsAccount savingsAccount = list.get(0);


        assertThat(savingsAccount.getBalance()).isEqualTo(0);
        assertThat(savingsAccount.getRateOfInterest()).isEqualTo(0);
        assertThat(savingsAccount.getUser().getUsername()).isEqualTo("default");
        assertThat(savingsAccount.getUser().getPassword()).isEqualTo("default");
    }

    @Test
    void transferToSavingsAccount() {
        long amount = 1_000_000L;

        userService.register(new UserCreateRequest("default", "default"), amount);
        User user = userRepository.findByUsername("default").orElseThrow();
        savingsAccountService.createSavingsAccount(user);

        List<SavingsAccount> list = savingsAccountRepository.findByUserIdOrderByIdAsc(user.getId());
        SavingsAccount savingsAccount = list.get(0);

        //when
        savingsAccountService.transferToSavingsAccount(user.getId(), savingsAccount.getId(), amount);

        Account afterAccount = accountRepository.findByUserId(user.getId()).orElseThrow();
        SavingsAccount afterSavingsAccount = savingsAccountRepository.findById(savingsAccount.getId()).orElseThrow();
        user = userRepository.findById(user.getId()).orElseThrow();

        //then
        assertThat(user.getUsed()).isEqualTo(amount);
        assertThat(afterAccount.getBalance()).isEqualTo(afterSavingsAccount.getBalance() - amount);

    }
}