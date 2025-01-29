package com.log_module.exception;

import com.log_module.logging.type.MDCKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
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

        errorLogMDCRequestDetails();
        log.error("Common exception occurred: code: {}, message: {}", errorCode.getCode(), errorCode.getMessage(), exception);
        return ResponseEntity.badRequest().body(errorResponse);
    }

    private void errorLogMDCRequestDetails() {
        String traceId = MDC.get(MDCKey.TRACE_ID.getKey());
        String requestURI = MDC.get(MDCKey.REQUEST_URI.getKey());
        String requestParams = MDC.get(MDCKey.REQUEST_PARAMS.getKey());
        String requestBody = MDC.get(MDCKey.REQUEST_BODY.getKey());
        String pathVariables = MDC.get(MDCKey.PATH_VARIABLES.getKey());
        String method = MDC.get(MDCKey.METHOD.getKey());

        String logMessage = String.format("""
                Request Info:
                ------------------------------
                TraceId       : %s
                RequestUri    : %s
                Method        : %s
                RequestParams : %s
                RequestBody   : %s
                PathVariables : %s
                ------------------------------
                """, traceId, requestURI, method, requestParams, requestBody, pathVariables);

        log.error(logMessage);
    }
}
