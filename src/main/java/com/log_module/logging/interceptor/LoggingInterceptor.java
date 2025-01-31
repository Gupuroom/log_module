package com.log_module.logging.interceptor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.log_module.logging.type.MDCKey;
import com.log_module.logging.wrapper.CustomHttpRequestWrapper;
import com.log_module.logging.wrapper.CustomHttpResponseWrapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    // Root Project .yml value
    @Value("${log-module.config.response-length-limit:200}")
    private int responseLengthLimit;
    @Value("${log-module.config.active:true}")
    private boolean isActive;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String traceId = UUID.randomUUID().toString();
        MDC.put(MDCKey.TRACE_ID.getKey(), traceId);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if(isActive) {
            String traceId = MDC.get(MDCKey.TRACE_ID.getKey());
            String requestURI = request.getRequestURI();
            String method = request.getMethod();

            if (response.getStatus() == HttpServletResponse.SC_OK) {
                String responseBody = extractResponseBody(response);
                log.info("Response Status: {} TraceId: {} Method: {} requestURI: {} responseBody:{}", response.getStatus(), traceId, method, requestURI, responseBody);
            } else {
                String requestParams = extractRequestParams(request);
                String pathVariables = extractPathVariables(handler);
                String requestBody = extractRequestBody(request);

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
        MDC.clear();
    }

    private String extractResponseBody(HttpServletResponse response) {
        if (response instanceof CustomHttpResponseWrapper responseWrapper) {
            byte[] responseData = responseWrapper.getResponseData();
            if (responseData != null && responseData.length > 0) {
                String responseString = new String(responseData);
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    Object json = mapper.readValue(responseString, Object.class);
                    String formattedResponse = mapper.writeValueAsString(json);

                    // 글자수 제한 처리
                    return truncateIfExceedsLimit(formattedResponse);
                } catch (JsonProcessingException e) {
                    return truncateIfExceedsLimit(responseString);
                }
            }
        }

        return null;
    }

    private String truncateIfExceedsLimit(String input) {
        if (input.length() > responseLengthLimit)
            return input.substring(0, responseLengthLimit) + "...";

        return input;
    }

    private String extractRequestParams(HttpServletRequest request) {
        Map<String, String> paramMap = new HashMap<>();
        Enumeration<String> parameterNames = request.getParameterNames();

        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            paramMap.put(paramName, request.getParameter(paramName));
        }

        return paramMap.isEmpty() ? null : paramMap.toString();
    }

    private String extractRequestBody(HttpServletRequest request) {
        if (request instanceof CustomHttpRequestWrapper requestWrapper) {
            String requestBody = new String(requestWrapper.getRequestBody()).replaceAll("\\s+", "");

            return requestBody.isEmpty() ? null : requestBody;
        }

        return null;
    }


    private String extractPathVariables(Object handler) {
        if (handler instanceof HandlerMethod) {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes != null) {
                HttpServletRequest currentRequest = attributes.getRequest();
                Map<String, String> pathVariables = (Map<String, String>) currentRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

                return (pathVariables == null || pathVariables.isEmpty()) ? null : pathVariables.toString();
            }
        }

        return null;
    }
}
