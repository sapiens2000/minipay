package com.minipay.account;

import com.minipay.user.UserRepository;
import com.minipay.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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




    @Test
    void createSavingsAccount() {

    }

    @Test
    void transferToSavingsAccount() {
    }
}