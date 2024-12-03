/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.confluence.event.events.label;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.event.api.AsynchronousPreferred;

@AsynchronousPreferred
@EventName(value="confluence.labels.label-manager-metrics")
public class LabelManagerMetricsEvent
extends ConfluenceEvent {
    private static final long serialVersionUID = 6463499783857742744L;
    private final String methodName;
    private final long durationMs;
    private final int maxResults;
    private final int resultsCount;

    public LabelManagerMetricsEvent(Object src, String methodName, long durationMs, int maxResults, int resultsCount) {
        super(src);
        this.methodName = methodName;
        this.durationMs = durationMs;
        this.maxResults = maxResults;
        this.resultsCount = resultsCount;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public long getDurationMs() {
        return this.durationMs;
    }

    public int getMaxResults() {
        return this.maxResults;
    }

    public int getResultsCount() {
        return this.resultsCount;
    }
}

