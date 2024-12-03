/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.event.events.admin.ReindexFinishedEvent
 *  com.atlassian.confluence.internal.index.lucene.FullReindexManager
 *  com.atlassian.confluence.search.ReIndexTask
 *  com.atlassian.confluence.util.DefaultClock
 *  com.atlassian.core.util.Clock
 *  com.atlassian.diagnostics.internal.ipd.IpdMainRegistry
 *  com.atlassian.diagnostics.internal.ipd.IpdMetricBuilder
 *  com.atlassian.diagnostics.internal.ipd.metrics.wrapper.IpdValueAndStatsMetricWrapper
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdJob
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdJobRunner
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.util.profiling.MetricTag$RequiredMetricTag
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 */
package com.atlassian.confluence.internal.diagnostics.ipd.index;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.event.events.admin.ReindexFinishedEvent;
import com.atlassian.confluence.internal.index.lucene.FullReindexManager;
import com.atlassian.confluence.search.ReIndexTask;
import com.atlassian.confluence.util.DefaultClock;
import com.atlassian.core.util.Clock;
import com.atlassian.diagnostics.internal.ipd.IpdMainRegistry;
import com.atlassian.diagnostics.internal.ipd.IpdMetricBuilder;
import com.atlassian.diagnostics.internal.ipd.metrics.wrapper.IpdValueAndStatsMetricWrapper;
import com.atlassian.diagnostics.ipd.internal.spi.IpdJob;
import com.atlassian.diagnostics.ipd.internal.spi.IpdJobRunner;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.util.profiling.MetricTag;
import java.time.Instant;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class ReIndexingDurationIpdJob
implements IpdJob {
    private final IpdValueAndStatsMetricWrapper totalTimeMetric;
    private final EventPublisher eventPublisher;
    private final FullReindexManager fullReindexManager;
    private final Clock clock;

    public ReIndexingDurationIpdJob(IpdJobRunner ipdJobRunner, IpdMainRegistry ipdMainRegistry, EventPublisher eventPublisher, FullReindexManager fullReindexManager) {
        this(ipdJobRunner, ipdMainRegistry, eventPublisher, fullReindexManager, (Clock)new DefaultClock());
    }

    @VisibleForTesting
    public ReIndexingDurationIpdJob(IpdJobRunner ipdJobRunner, IpdMainRegistry ipdMainRegistry, EventPublisher eventPublisher, FullReindexManager fullReindexManager, Clock clock) {
        this.eventPublisher = eventPublisher;
        this.fullReindexManager = fullReindexManager;
        this.clock = clock;
        Objects.requireNonNull(ipdJobRunner).register((IpdJob)this);
        this.totalTimeMetric = Objects.requireNonNull(ipdMainRegistry).createRegistry(IpdMetricBuilder::logOnUpdate).valueAndStatsMetric("index.rebuild.totalTimeMillis", new MetricTag.RequiredMetricTag[0]);
    }

    public void runJob() {
    }

    @PostConstruct
    void init() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void unregisterForEvents() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void onReIndexJobFinishedEvent(ReindexFinishedEvent reIndexFinishedEvent) {
        ReIndexTask lastReindexingTask = this.fullReindexManager.getLastReindexingTask();
        Instant instant = this.clock.getCurrentDate().toInstant();
        if (lastReindexingTask != null) {
            long reindexingTimeMillis = instant.toEpochMilli() - lastReindexingTask.getStartTime();
            this.totalTimeMetric.update(Long.valueOf(reindexingTimeMillis));
        }
    }
}

