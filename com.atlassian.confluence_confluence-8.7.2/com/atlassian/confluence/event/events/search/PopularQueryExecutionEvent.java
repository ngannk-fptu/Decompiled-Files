/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.confluence.event.events.search;

import com.atlassian.analytics.api.annotations.EventName;

@EventName(value="confluence.search.popularQueryExecution")
public class PopularQueryExecutionEvent {
    private final long durationMillis;

    public PopularQueryExecutionEvent(long startMillis, long endMillis) {
        this.durationMillis = endMillis - startMillis;
    }

    public long getDurationMillis() {
        return this.durationMillis;
    }
}

