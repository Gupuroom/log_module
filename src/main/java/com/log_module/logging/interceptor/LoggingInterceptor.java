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

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String traceId = UUID.randomUUID().toString(); // 새로운 Trace ID 생성
        MDC.put(MDCKey.TRACE_ID.getKey(), traceId);
        MDC.put(MDCKey.REQUEST_URI.getKey(), request.getRequestURI());
        MDC.put(MDCKey.METHOD.getKey(), request.getMethod());

        if(request.getMethod().equals("GET")) {
            putRequestParamsToMDC(request);  // Request Param Logging
            putPathVariablesToMDC(handler); // PathVariable Logging
        }

        if(request.getMethod().equals("POST")) {
            putRequestParamsToMDC(request);  // Request Param Logging
            putRequestBodyToMDC(request); // Request Body Logging
            putPathVariablesToMDC(handler); // PathVariable Logging
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws JsonProcessingException {
        String traceId = MDC.get(MDCKey.TRACE_ID.getKey());
        String requestURI = MDC.get(MDCKey.REQUEST_URI.getKey());
        String method = MDC.get(MDCKey.METHOD.getKey());

        String responseBody = getResponseBody(response);
        if (responseBody != null) {
            log.info("Response Status: [{}] TraceId: [{}] Method: [{}] requestURI: [{}] Body: [{}]", response.getStatus(), traceId, method, requestURI, responseBody);
        } else {
            log.info("Response Status: [{}] TraceId: [{}] Method: [{}] requestURI: [{}]", response.getStatus(), traceId, method, requestURI);
        }

        MDC.clear();
    }

    private String getResponseBody(HttpServletResponse response) {
        if (response instanceof CustomHttpResponseWrapper responseWrapper) {
            byte[] responseData = responseWrapper.getResponseData();
            if (responseData != null && responseData.length > 0) {
                String responseString = new String(responseData);
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    Object json = mapper.readValue(responseString, Object.class);
                    String formattedResponse = mapper.writeValueAsString(json);

                    // 글자수 제한 처리
                    return truncateIfExceedsLimit(formattedResponse, 200);
                } catch (JsonProcessingException e) {
                    return truncateIfExceedsLimit(responseString, 200);
                }
            }
        }

        return null;
    }

    private String truncateIfExceedsLimit(String input, int limit) {
        if (input.length() > limit) {
            return input.substring(0, limit) + "...";
        }
        return input;
    }

    private void putRequestParamsToMDC(HttpServletRequest request) {
        Enumeration<String> parameterNames = request.getParameterNames();
        if (parameterNames.hasMoreElements()) {
            Map<String, String> paramMap = new HashMap<>();

            while (parameterNames.hasMoreElements()) {
                String paramName = parameterNames.nextElement();
                paramMap.put(paramName, request.getParameter(paramName));
            }

            MDC.put(MDCKey.REQUEST_PARAMS.getKey(), paramMap.toString());
        }
    }

    private void putRequestBodyToMDC(HttpServletRequest request) {
        if (request instanceof CustomHttpRequestWrapper requestWrapper) {
            String requestBody = new String(requestWrapper.getRequestBody()).replaceAll("\\s+", "");

            if (!requestBody.isEmpty()) {
                MDC.put(MDCKey.REQUEST_BODY.getKey(), requestBody);
            }
        }
    }

    private void putPathVariablesToMDC(Object handler) {
        if (handler instanceof HandlerMethod) {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest currentRequest = attributes.getRequest();
                Map<String, String> pathVariables = (Map<String, String>) currentRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

                if (pathVariables != null && !pathVariables.isEmpty()) {
                    MDC.put(MDCKey.PATH_VARIABLES.getKey(), pathVariables.toString());
                }
            }
        }
    }
}
