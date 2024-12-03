/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.logging.LoggingContext
 */
package com.atlassian.confluence.impl.logging;

import com.atlassian.confluence.util.logging.LoggingContext;
import java.util.Iterator;
import java.util.Map;

public final class LoggingContextRenderer {
    private static final String CONTEXT_LEADER = " -- ";
    private static final String MDC_ENTRY_SEPARATOR = " | ";
    private static final String MDC_KEY_VALUE_SEPARATOR = ": ";

    private LoggingContextRenderer() {
    }

    public static CharSequence renderLoggingContext() {
        if (LoggingContext.isEmpty()) {
            return "";
        }
        StringBuilder buffer = new StringBuilder();
        buffer.append(CONTEXT_LEADER);
        Map context = LoggingContext.getContext();
        Iterator entryIterator = context.entrySet().iterator();
        while (entryIterator.hasNext()) {
            Map.Entry entry = entryIterator.next();
            buffer.append((String)entry.getKey()).append(MDC_KEY_VALUE_SEPARATOR).append(entry.getValue());
            if (!entryIterator.hasNext()) continue;
            buffer.append(MDC_ENTRY_SEPARATOR);
        }
        buffer.append(System.lineSeparator());
        return buffer;
    }
}

