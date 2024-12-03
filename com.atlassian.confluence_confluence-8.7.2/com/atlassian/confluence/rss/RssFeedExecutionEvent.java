/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.confluence.rss;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.event.api.AsynchronousPreferred;

@AsynchronousPreferred
@EventName(value="confluence.rssfeed.execution")
public class RssFeedExecutionEvent {
    private final String feedType;

    public RssFeedExecutionEvent(String feedType) {
        this.feedType = feedType;
    }

    public String getFeedType() {
        return this.feedType;
    }
}

