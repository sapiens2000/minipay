package com.minipay.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // User
    DUPLICATE_NAME(400, "이미 가입한 유저입니다."),
    NOT_FOUND_USER(404, "조회된 멤버가 없습니다."),

    // Account
    NOT_FOUND_ACCOUNT(404, "조회된 계좌가 없습니다."),
    DAILY_LIMIT_EXCEEDED(400, "일일 출금 한도를 초과했습니다."),
    INSUFFICIENT_BALANCE(400, "잔액이 부족합니다.");

    private final int status;
    private final String message;
}