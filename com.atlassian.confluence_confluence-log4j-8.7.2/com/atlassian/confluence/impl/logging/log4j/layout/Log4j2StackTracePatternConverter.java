/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.impl.logging.ConfluenceStackTraceRenderer
 *  org.apache.logging.log4j.core.LogEvent
 *  org.apache.logging.log4j.core.config.plugins.Plugin
 *  org.apache.logging.log4j.core.pattern.ConverterKeys
 *  org.apache.logging.log4j.core.pattern.LogEventPatternConverter
 */
package com.atlassian.confluence.impl.logging.log4j.layout;

import com.atlassian.confluence.impl.logging.ConfluenceStackTraceRenderer;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;

@Plugin(name="ConfluenceStackTrace", category="Converter")
@ConverterKeys(value={"stacktrace"})
public final class Log4j2StackTracePatternConverter
extends LogEventPatternConverter {
    static final String NAME = "ConfluenceStackTrace";
    static final String STACK_TRACE_PATTERN_KEY = "stacktrace";

    Log4j2StackTracePatternConverter() {
        super(NAME, null);
    }

    public static Log4j2StackTracePatternConverter newInstance(String[] options) {
        return new Log4j2StackTracePatternConverter();
    }

    public void format(LogEvent event, StringBuilder toAppendTo) {
        if (event.getThrown() != null) {
            toAppendTo.append(ConfluenceStackTraceRenderer.renderStackTrace((Throwable)event.getThrown()));
        }
    }
}

