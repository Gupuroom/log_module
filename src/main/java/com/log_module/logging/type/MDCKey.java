package com.log_module.logging.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MDCKey {
    TRACE_ID("traceId"),
    METHOD("method"),
    REQUEST_URI("requestURI"),
    REQUEST_PARAMS("requestParams"),
    REQUEST_BODY("requestBody"),
    PATH_VARIABLES("pathVariables");

    private final String key;
}