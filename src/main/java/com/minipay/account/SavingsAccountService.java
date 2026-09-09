package com.minipay.account;

import com.minipay.common.CustomException;
import com.minipay.common.ErrorCode;
import com.minipay.user.User;
import com.minipay.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SavingsAccountService {

    private final SavingsAccountRepository savingsAccountRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createSavingsAccount(User user){
        SavingsAccount savingsAccount = SavingsAccount.createSavingsAccount(user);
        savingsAccountRepository.save(savingsAccount);
        user.getSavingsAccount().add(savingsAccount);
    }

    /*
     * 내 메인 게좌에서 적금 계좌로 인출하는 함수
     * 1. 유저 한도 조회(비관락, findByIdForUpdate)
     * 2. 적금 계좌에 송금(비관락, deposit)
     * 두 흐름을 하나의 트랜잭션 경계로 설정
     * */
    @Transactional
    public void transferToSavingsAccount(Long userId, Long savingsAccountId, long amount){
        validateDailyLimit(userId, amount);
        SavingsAccount savingsAccount = savingsAccountRepository.getSavingsAccountWithLock(savingsAccountId);

    }

    public void validateDailyLimit(Long userId, long amount){
        User user = userRepository.getUserWithLock(userId);
        long dailyLimit = user.getDailyLimit();

        if(dailyLimit == 0){
            throw new CustomException(ErrorCode.INSUFFICIENT_BALANCE);
        }

        if(dailyLimit < user.getUsed() + amount){
            throw new CustomException(ErrorCode.DAILY_LIMIT_EXCEEDED);
        }
    }
}
