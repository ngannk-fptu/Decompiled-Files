/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.impl.metrics;

import com.atlassian.confluence.event.events.analytics.MonitoringStatsAnalyticEvent;
import com.atlassian.confluence.impl.metrics.ConfluenceJmxConfigService;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import java.util.Objects;
import javax.annotation.Nonnull;

public class MonitoringStatsEventPublisher
implements JobRunner {
    private final ConfluenceJmxConfigService confluenceJmxConfigService;
    private final EventPublisher eventPublisher;

    public MonitoringStatsEventPublisher(ConfluenceJmxConfigService confluenceJmxConfigService, EventPublisher eventPublisher) {
        this.confluenceJmxConfigService = Objects.requireNonNull(confluenceJmxConfigService);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Nonnull
    public JobRunnerResponse runJob(JobRunnerRequest ignored) {
        this.eventPublisher.publish((Object)new MonitoringStatsAnalyticEvent(this.confluenceJmxConfigService.isJmxEnabledOnCluster(), this.confluenceJmxConfigService.isAppMonitoringEnabled(), this.confluenceJmxConfigService.isIpdMonitoringEnabled()));
        return JobRunnerResponse.success();
    }
}

