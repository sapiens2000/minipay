package com.minipay.account;

import com.minipay.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SavingsAccountService {

    private final SavingsAccountRepository savingsAccountRepository;

    @Transactional
    public void createSavingsAccount(User user){
        SavingsAccount savingsAccount = SavingsAccount.createSavingsAccount(user);
        user.getSavingsAccount().add(savingsAccount);
        savingsAccountRepository.save(savingsAccount);
    }

    public void transferToSavingsAccount(){
        /*
        *
        *
        * */
    }
}
