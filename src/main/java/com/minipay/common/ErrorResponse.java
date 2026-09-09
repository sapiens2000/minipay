package com.minipay.common;

public record ErrorResponse(
        int status,
        String message
) {

}