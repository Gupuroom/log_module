package com.log_module.exception;

import com.log_module.logging.type.MDCKey;
import lombok.Builder;
import lombok.Getter;
import org.slf4j.MDC;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommonExceptionResponse {
    private String code;
    private String message;
    private String traceId;
    private LocalDateTime timestamp;

    public static CommonExceptionResponse of(CommonExceptionCode errorCode) {
        String traceId = MDC.get(MDCKey.TRACE_ID.getKey());
        return CommonExceptionResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .traceId(traceId)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
