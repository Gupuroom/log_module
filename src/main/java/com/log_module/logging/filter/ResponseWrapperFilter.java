package com.log_module.logging.filter;

import com.log_module.logging.wrapper.CustomHttpResponseWrapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ResponseWrapperFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (response instanceof HttpServletResponse) {
            CustomHttpResponseWrapper responseWrapper = new CustomHttpResponseWrapper((HttpServletResponse) response);
            chain.doFilter(request, responseWrapper);

            // 응답 데이터가 null이거나 비어있지 않은 경우에만 클라이언트로 출력
            byte[] responseData = responseWrapper.getResponseData();
            if (responseData != null && responseData.length > 0) {
                response.getOutputStream().write(responseData);
            }
        } else {
            chain.doFilter(request, response);
        }
    }
}