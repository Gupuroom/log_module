package com.log_module.logging.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import org.springframework.stereotype.Component;

@Component
public class ERRORLogFilter extends Filter<LoggingEvent> {

    @Override
    public FilterReply decide(LoggingEvent event) {
        Level level = event.getLevel();
        if (level == Level.ERROR) {
            return FilterReply.ACCEPT;
        }

        return FilterReply.DENY;
    }
}