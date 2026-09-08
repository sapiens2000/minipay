package com.minipay.user;

public record UserCreateRequest(
        String username,
        String password) {

    public User toUserEntity() {
        User user = new User();
        user.setDefault(this.username, this.password);
        return user;
    }

}
