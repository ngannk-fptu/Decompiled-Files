/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.spi.LoggingEvent
 */
package com.atlassian.core.logging;

import java.util.Date;
import org.apache.log4j.spi.LoggingEvent;

@Deprecated
public class DatedLoggingEvent {
    private final long timeInMillis;
    private final LoggingEvent event;

    public DatedLoggingEvent(long timeInMillis, LoggingEvent event) {
        this.timeInMillis = timeInMillis;
        this.event = event;
    }

    public LoggingEvent getEvent() {
        return this.event;
    }

    public long getTimeInMillis() {
        return this.timeInMillis;
    }

    public Date getDate() {
        return new Date(this.timeInMillis);
    }
}

