/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.confluence.internal.diagnostics;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.event.api.AsynchronousPreferred;

@AsynchronousPreferred
@EventName(value="confluence.diagnostics.long-running-task")
public class LongRunningTaskAnalyticsEvent {
    private final String className;
    private final int percentageComplete;
    private final long elapsedTimeInSec;
    private final long estimatedTimeRemainingInSec;
    private final long thresholdInSec;
    private final String threadState;

    public LongRunningTaskAnalyticsEvent(String className, int percentageComplete, long elapsedTimeInSec, long estimatedTimeRemainingInSec, long thresholdInSec, Thread.State threadState) {
        this.className = className;
        this.percentageComplete = percentageComplete;
        this.elapsedTimeInSec = elapsedTimeInSec;
        this.estimatedTimeRemainingInSec = estimatedTimeRemainingInSec;
        this.thresholdInSec = thresholdInSec;
        this.threadState = threadState.name();
    }

    public int getPercentageComplete() {
        return this.percentageComplete;
    }

    public long getElapsedTimeInSec() {
        return this.elapsedTimeInSec;
    }

    public long getEstimatedTimeRemainingInSec() {
        return this.estimatedTimeRemainingInSec;
    }

    public long getThresholdInSec() {
        return this.thresholdInSec;
    }

    public String getThreadState() {
        return this.threadState;
    }

    public String getClassName() {
        return this.className;
    }
}

