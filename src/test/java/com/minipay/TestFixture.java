package com.minipay;

import com.minipay.user.User;

public class TestFixture {

    public User createUserWithBalance(String username, String password, long amount){
        User user = new User();
        user.setDefault(username, password);
        user.setBalance(amount);
        return user;
    }
}
