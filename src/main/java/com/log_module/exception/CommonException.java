package com.log_module.exception;

import lombok.Getter;

@Getter
public class CommonException extends RuntimeException {
    private final CommonErrorCode errorCode;

    public CommonException(CommonErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}