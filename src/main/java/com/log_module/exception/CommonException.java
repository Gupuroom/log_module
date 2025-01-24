package com.log_module.exception;

import lombok.Getter;

@Getter
public class CommonException extends RuntimeException {
    private final CommonExceptionCode errorCode;

    public CommonException(CommonExceptionCode errorCode) {
        this.errorCode = errorCode;
    }
}