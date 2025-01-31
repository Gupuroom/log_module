package com.log_module.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class CommonExceptionHandler {

    @ExceptionHandler(CommonException.class)
    public ResponseEntity<CommonExceptionResponse> handleCustomException(CommonException exception) {
        CommonErrorCode errorCode = exception.getErrorCode();
        CommonExceptionResponse errorResponse = CommonExceptionResponse.of(errorCode);

        log.error("Common exception occurred: code: {}, message: {}", errorCode.getCode(), errorCode.getMessage(), exception);
        return ResponseEntity.badRequest().body(errorResponse);
    }
}
