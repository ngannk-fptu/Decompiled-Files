/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.task.longrunning.LongRunningTask
 *  com.atlassian.diagnostics.MonitoringService
 *  com.atlassian.diagnostics.Severity
 *  com.atlassian.diagnostics.detail.ThreadDumpProducer
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.collect.ImmutableMap
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.diagnostics;

import com.atlassian.confluence.internal.diagnostics.AlertTriggerFactory;
import com.atlassian.confluence.internal.diagnostics.ConfluenceMonitor;
import com.atlassian.confluence.internal.diagnostics.DiagnosticsInfo;
import com.atlassian.confluence.internal.diagnostics.DiagnosticsWorker;
import com.atlassian.confluence.internal.diagnostics.LongRunningTaskAnalyticsEvent;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.core.task.longrunning.LongRunningTask;
import com.atlassian.diagnostics.MonitoringService;
import com.atlassian.diagnostics.Severity;
import com.atlassian.diagnostics.detail.ThreadDumpProducer;
import com.atlassian.event.api.EventPublisher;
import com.google.common.collect.ImmutableMap;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LongRunningTaskMonitor
extends ConfluenceMonitor {
    private static final long SLOW_LONG_RUNNING_TASK_SECS = Integer.getInteger("diagnostics.slow.long.running.task.secs", 300).intValue();
    private static final int TASK_EXCEEDS_TIME_LIMIT_ID = 1001;
    private static final Logger logger = LoggerFactory.getLogger(LongRunningTaskMonitor.class);
    private static final String MONITOR_ID = "JOB";
    private final Map<LongRunningTask, DiagnosticsInfo> tasks = new ConcurrentHashMap<LongRunningTask, DiagnosticsInfo>();
    private final ThreadDumpProducer threadDumpProducer;
    private final AlertTriggerFactory alertTriggerFactory;
    private final EventPublisher eventPublisher;

    public LongRunningTaskMonitor(@NonNull ThreadDumpProducer threadDumpProducer, @NonNull AlertTriggerFactory alertTriggerFactory, @NonNull EventPublisher eventPublisher) {
        this.threadDumpProducer = Objects.requireNonNull(threadDumpProducer);
        this.alertTriggerFactory = Objects.requireNonNull(alertTriggerFactory);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Override
    public void init(MonitoringService monitoringService) {
        super.init(monitoringService);
        this.monitor = monitoringService.createMonitor(MONITOR_ID, "diagnostics.long.running.task.name", () -> true);
        this.defineIssue("diagnostics.long.running.task.issue", 1001, Severity.WARNING);
        this.startMonitorThread();
        logger.debug("{} monitor has been initialized", (Object)MONITOR_ID);
    }

    @Override
    protected String getMonitorId() {
        return MONITOR_ID;
    }

    public void start(LongRunningTask task) {
        this.tasks.put(task, new DiagnosticsInfo(Thread.currentThread(), AuthenticatedUserThreadLocal.getUsername(), Duration.ofSeconds(SLOW_LONG_RUNNING_TASK_SECS)));
    }

    public void stop(LongRunningTask task) {
        this.tasks.remove(task);
    }

    private void startMonitorThread() {
        this.startMonitorThread(new DiagnosticsWorker<LongRunningTask>(this.tasks, this::alert, Duration.ofSeconds(SLOW_LONG_RUNNING_TASK_SECS)), "diagnostics-long-running-task-thread");
    }

    private void alert(LongRunningTask task, DiagnosticsInfo info) {
        this.alert(1001, builder -> {
            builder.timestamp(Instant.now()).trigger(this.alertTriggerFactory.create(task.getClass())).details(() -> ImmutableMap.builder().put((Object)"username", (Object)info.getUsername().orElse("")).put((Object)"taskKey", (Object)task.getNameKey()).put((Object)"className", (Object)task.getClass().getSimpleName()).put((Object)"taskName", (Object)task.getName()).put((Object)"taskStatus", (Object)task.getCurrentStatus()).put((Object)"taskElapsedTime", (Object)task.getElapsedTime()).put((Object)"taskEstimatedTimeRemaining", (Object)task.getEstimatedTimeRemaining()).put((Object)"taskPercentageComplete", (Object)task.getPercentageComplete()).put((Object)"thresholdInSecs", (Object)info.getTimeLimit().getSeconds()).put((Object)"threadId", (Object)info.getWorkerThread().getId()).put((Object)"threadName", (Object)info.getWorkerThread().getName()).put((Object)"threadStatus", (Object)info.getWorkerThread().getState()).put((Object)"threadDump", (Object)this.threadDumpProducer.produce(Collections.singleton(info.getWorkerThread()))).build());
            this.eventPublisher.publish((Object)new LongRunningTaskAnalyticsEvent(task.getClass().getSimpleName(), task.getPercentageComplete(), task.getElapsedTime(), task.getEstimatedTimeRemaining(), info.getTimeLimit().getSeconds(), info.getWorkerThread().getState()));
        });
    }
}

