package com.log_module.test.type;

import com.log_module.exception.CommonErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TestExceptionCode implements CommonErrorCode {
    SUCCESS("200","SUCCESS"),
    GET_TEST_ERROR("301","GET_TEST_ERROR"),
    POST_TEST_ERROR("302","POST_TEST_ERROR"),
    DELETE_TEST_ERROR("303","DELETE_TEST_ERROR"),
    ASYNC_TEST_ERROR("304","ASYNC_TEST_ERROR");

    private final String code;
    private final String message;
}