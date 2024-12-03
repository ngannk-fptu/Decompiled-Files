/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.confluence.event.events.search;

import com.atlassian.analytics.api.annotations.EventName;

@EventName(value="confluence.search.contentNameQueryExecution")
public class ContentNameQueryExecutionEvent {
    private final long durationMillis;

    public ContentNameQueryExecutionEvent(long startMillis, long endMillis) {
        this.durationMillis = endMillis - startMillis;
    }

    public long getDurationMillis() {
        return this.durationMillis;
    }
}

