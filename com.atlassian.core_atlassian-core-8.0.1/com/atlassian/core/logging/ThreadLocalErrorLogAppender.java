/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.AppenderSkeleton
 *  org.apache.log4j.spi.LoggingEvent
 */
package com.atlassian.core.logging;

import com.atlassian.core.logging.ThreadLocalErrorCollection;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

@Deprecated
public class ThreadLocalErrorLogAppender
extends AppenderSkeleton {
    protected void append(LoggingEvent event) {
        ThreadLocalErrorCollection.add(System.currentTimeMillis(), event);
    }

    public void close() {
    }

    public boolean requiresLayout() {
        return false;
    }
}

