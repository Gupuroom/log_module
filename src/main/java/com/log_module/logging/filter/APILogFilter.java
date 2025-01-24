package com.log_module.logging.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import com.log_module.logging.type.MDCKey;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class APILogFilter extends Filter<LoggingEvent> {

    @Override
    public FilterReply decide(LoggingEvent event) {
        Level level = event.getLevel();
        String traceId = MDC.get(MDCKey.TRACE_ID.getKey());
        String requestURI = MDC.get(MDCKey.REQUEST_URI.getKey());
        String method = MDC.get(MDCKey.METHOD.getKey());

        if (Level.INFO.equals(level) && traceId != null && requestURI != null && method != null) {
            return FilterReply.ACCEPT;
        }

        return FilterReply.DENY;
    }
}