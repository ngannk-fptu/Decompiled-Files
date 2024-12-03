/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.concurrent.Timeout
 *  io.atlassian.util.concurrent.Timeout
 */
package com.atlassian.confluence.rss;

import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.event.events.ConfluenceEvent;
import io.atlassian.util.concurrent.Timeout;
import java.util.concurrent.TimeUnit;

public class FeedTimeoutEvent
extends ConfluenceEvent {
    private static final long serialVersionUID = -5579269621168820309L;
    private final ConfluenceEntityObject entity;
    private final long allowedTimeInSeconds;
    private final long exceededTimeInMilliseconds;
    private final int numResultsToRender;
    private final int numResultsRendered;

    @Deprecated
    public static FeedTimeoutEvent forTimeout(Object src, ConfluenceEntityObject entity, com.atlassian.util.concurrent.Timeout timeout, int numResultsToRender, int numResultsRendered) {
        return FeedTimeoutEvent.createForTimeout(src, entity, Timeout.getNanosTimeout((long)timeout.getTime(), (TimeUnit)timeout.getUnit()), numResultsToRender, numResultsRendered);
    }

    public static FeedTimeoutEvent createForTimeout(Object src, ConfluenceEntityObject entity, Timeout timeout, int numResultsToRender, int numResultsRendered) {
        long exceededTimeInMilliseconds = timeout.getUnit().toMillis(-timeout.getTime());
        long allowedTimeInSeconds = timeout.getUnit().toSeconds(timeout.getTimeoutPeriod());
        return new FeedTimeoutEvent(src, entity, allowedTimeInSeconds, exceededTimeInMilliseconds, numResultsToRender, numResultsRendered);
    }

    public FeedTimeoutEvent(Object src, ConfluenceEntityObject entity, long allowedTimeInSeconds, long exceededTimeInMilliseconds, int numResultsToRender, int numResultsRendered) {
        super(src);
        this.entity = entity;
        this.allowedTimeInSeconds = allowedTimeInSeconds;
        this.exceededTimeInMilliseconds = exceededTimeInMilliseconds;
        this.numResultsToRender = numResultsToRender;
        this.numResultsRendered = numResultsRendered;
    }

    public ConfluenceEntityObject getEntity() {
        return this.entity;
    }

    public long getAllowedTimeInSeconds() {
        return this.allowedTimeInSeconds;
    }

    public long getExceededTimeInMilliseconds() {
        return this.exceededTimeInMilliseconds;
    }

    public int getNumResultsToRender() {
        return this.numResultsToRender;
    }

    public int getNumResultsRendered() {
        return this.numResultsRendered;
    }
}

