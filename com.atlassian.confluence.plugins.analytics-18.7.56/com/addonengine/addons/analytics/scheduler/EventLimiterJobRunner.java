/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.scheduler;

import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.config.JobRunnerKey;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b&\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002R\u0011\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0007"}, d2={"Lcom/addonengine/addons/analytics/scheduler/EventLimiterJobRunner;", "Lcom/atlassian/scheduler/JobRunner;", "()V", "jobRunnerKey", "Lcom/atlassian/scheduler/config/JobRunnerKey;", "getJobRunnerKey", "()Lcom/atlassian/scheduler/config/JobRunnerKey;", "analytics"})
public abstract class EventLimiterJobRunner
implements JobRunner {
    @NotNull
    private final JobRunnerKey jobRunnerKey;

    public EventLimiterJobRunner() {
        JobRunnerKey jobRunnerKey = JobRunnerKey.of((String)"AnalyticsForConfluence.EventLimiter");
        Intrinsics.checkNotNull((Object)jobRunnerKey);
        this.jobRunnerKey = jobRunnerKey;
    }

    @NotNull
    public final JobRunnerKey getJobRunnerKey() {
        return this.jobRunnerKey;
    }
}

