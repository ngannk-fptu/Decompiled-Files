/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.impl.logging.LoggingContextRenderer
 *  org.apache.logging.log4j.Level
 *  org.apache.logging.log4j.core.LogEvent
 *  org.apache.logging.log4j.core.config.plugins.Plugin
 *  org.apache.logging.log4j.core.pattern.ConverterKeys
 *  org.apache.logging.log4j.core.pattern.LogEventPatternConverter
 */
package com.atlassian.confluence.impl.logging.log4j.layout;

import com.atlassian.confluence.impl.logging.LoggingContextRenderer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;

@Plugin(name="ConfluenceLoggingContext", category="Converter")
@ConverterKeys(value={"loggingcontext"})
public final class Log4j2LoggingContextPatternConverter
extends LogEventPatternConverter {
    static final String NAME = "ConfluenceLoggingContext";
    static final String LOGGING_CONTEXT_PATTERN_KEY = "loggingcontext";

    Log4j2LoggingContextPatternConverter() {
        super(NAME, null);
    }

    public static Log4j2LoggingContextPatternConverter newInstance(String[] options) {
        return new Log4j2LoggingContextPatternConverter();
    }

    public void format(LogEvent event, StringBuilder toAppendTo) {
        if (!Log4j2LoggingContextPatternConverter.isLowerPriorityThan(event, Level.WARN) || Log4j2LoggingContextPatternConverter.hasThrowable(event)) {
            toAppendTo.append(LoggingContextRenderer.renderLoggingContext());
        }
    }

    private static boolean hasThrowable(LogEvent event) {
        return event.getThrown() != null;
    }

    private static boolean isLowerPriorityThan(LogEvent event, Level threshold) {
        return !event.getLevel().isMoreSpecificThan(threshold);
    }
}

