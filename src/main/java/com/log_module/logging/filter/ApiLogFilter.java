package com.log_module.logging.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import com.log_module.logging.type.MDCKey;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class ApiLogFilter extends Filter<LoggingEvent> {

    @Override
    public FilterReply decide(LoggingEvent event) {
        Level level = event.getLevel();
        String traceId = MDC.get(MDCKey.TRACE_ID.getKey());
        String loggerName = event.getLoggerName();

        if (Level.INFO.equals(level) && traceId != null && loggerName.contains("LoggingInterceptor")) {
            return FilterReply.ACCEPT;
        }

        return FilterReply.DENY;
    }
}