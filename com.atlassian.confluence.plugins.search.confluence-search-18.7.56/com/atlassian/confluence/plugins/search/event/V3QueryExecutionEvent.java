/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.confluence.plugins.search.event;

import com.atlassian.analytics.api.annotations.EventName;

@EventName(value="confluence.search.v3QueryExecution")
public class V3QueryExecutionEvent {
    private final long durationMillis;

    public V3QueryExecutionEvent(long startMillis, long endMillis) {
        this.durationMillis = endMillis - startMillis;
    }

    public long getDurationMillis() {
        return this.durationMillis;
    }
}

