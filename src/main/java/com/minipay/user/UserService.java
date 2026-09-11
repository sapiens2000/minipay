package com.minipay.user;


import com.minipay.account.Account;
import com.minipay.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly=true)
public class UserService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;


    public User getUesr(Long id){
        return userRepository.findById(id).get();
    }

    /*
    * 유저 가입 시 호출하는 기본 생성 메서드
    * 유저/계좌 엔티티를 함께 생성
    * */
    @Transactional
    public void register(UserCreateRequest rq){
        User user = rq.toUserEntity();
        userRepository.save(user);
        accountRepository.save(Account.createMainAccount(user));
    }

}
