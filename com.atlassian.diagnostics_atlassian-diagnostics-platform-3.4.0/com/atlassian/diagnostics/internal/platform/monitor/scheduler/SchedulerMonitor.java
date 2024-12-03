/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.MonitorConfiguration
 *  com.atlassian.diagnostics.MonitoringService
 *  com.atlassian.diagnostics.Severity
 *  com.atlassian.diagnostics.internal.InitializingMonitor
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics.internal.platform.monitor.scheduler;

import com.atlassian.diagnostics.MonitorConfiguration;
import com.atlassian.diagnostics.MonitoringService;
import com.atlassian.diagnostics.Severity;
import com.atlassian.diagnostics.internal.InitializingMonitor;
import com.atlassian.diagnostics.internal.platform.monitor.scheduler.RunningJobDiagnostic;
import com.atlassian.diagnostics.internal.platform.monitor.scheduler.ScheduledJobDiagnostic;
import com.atlassian.diagnostics.internal.platform.monitor.scheduler.SchedulerMonitorConfiguration;
import com.atlassian.diagnostics.internal.platform.plugin.AlertTriggerResolver;
import com.atlassian.diagnostics.internal.platform.plugin.BundleFinder;
import com.google.common.collect.ImmutableMap;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public class SchedulerMonitor
extends InitializingMonitor {
    private static final String KEY_PREFIX = "diagnostics.scheduler.issue";
    private static final int HIGH_UTILIZATION_ISSUE_ID = 3001;
    private static final int SLOW_JOB_ISSUE_ID = 3002;
    private final SchedulerMonitorConfiguration schedulerMonitorConfiguration;
    private final AlertTriggerResolver alertTriggerResolver;
    private final BundleFinder bundleFinder;

    public SchedulerMonitor(SchedulerMonitorConfiguration schedulerMonitorConfiguration, AlertTriggerResolver alertTriggerResolver, BundleFinder bundleFinder) {
        this.schedulerMonitorConfiguration = schedulerMonitorConfiguration;
        this.alertTriggerResolver = Objects.requireNonNull(alertTriggerResolver, "alertTriggerResolver");
        this.bundleFinder = bundleFinder;
    }

    public void init(@Nonnull MonitoringService monitoringService) {
        this.monitor = monitoringService.createMonitor("SCHEDULER", "diagnostics.scheduler.name", (MonitorConfiguration)this.schedulerMonitorConfiguration);
        this.defineIssue(KEY_PREFIX, 3001, Severity.INFO);
        this.defineIssue(KEY_PREFIX, 3002, Severity.INFO);
    }

    public void raiseAlertForHighUtilization(@Nonnull Instant timestamp, @Nonnull List<RunningJobDiagnostic> runningJobs, int workerThreadCount) {
        this.alert(3001, builder -> builder.timestamp(timestamp).details(() -> this.highUtilizationAlertDetails(runningJobs, workerThreadCount)));
    }

    private Map<Object, Object> highUtilizationAlertDetails(List<RunningJobDiagnostic> runningJobs, int workerThreadCount) {
        List jobs = runningJobs.stream().map(job -> {
            long runningTime = System.currentTimeMillis() - job.getRunningJob().getStartTime().getTime();
            ImmutableMap.Builder alertBuilder = ImmutableMap.builder().put((Object)"jobRunnerKey", (Object)job.getRunningJob().getJobConfig().getJobRunnerKey().toString()).put((Object)"jobId", (Object)job.getRunningJob().getJobId().toString()).put((Object)"jobRunningTimeInMillis", (Object)runningTime);
            job.getJobRunner().ifPresent(jobRunner -> {
                alertBuilder.put((Object)"jobRunnerClass", (Object)jobRunner.getClass().getName());
                this.bundleFinder.getBundleNameForClass(jobRunner.getClass()).ifPresent(plugin -> alertBuilder.put((Object)"plugin", plugin));
            });
            return alertBuilder.build();
        }).collect(Collectors.toList());
        return ImmutableMap.builder().put((Object)"workerThreads", (Object)workerThreadCount).put((Object)"jobs", jobs).build();
    }

    public void raiseAlertForSlowJob(@Nonnull Instant timestamp, @Nonnull ScheduledJobDiagnostic diagnostic) {
        this.alert(3002, builder -> builder.trigger(this.alertTriggerResolver.triggerForBundle(diagnostic.getJobRunner().map(Object::getClass).orElse(null))).timestamp(timestamp).details(() -> this.slowJobAlertDetails(diagnostic)));
    }

    private Map<Object, Object> slowJobAlertDetails(ScheduledJobDiagnostic diagnostic) {
        ImmutableMap.Builder builder = ImmutableMap.builder().put((Object)"jobRunnerKey", (Object)diagnostic.getJobDetails().getJobRunnerKey().toString()).put((Object)"jobId", (Object)diagnostic.getJobDetails().getJobId().toString());
        diagnostic.getLastRun().ifPresent(lastRun -> builder.put((Object)"jobDurationInMillis", (Object)diagnostic.getLastRun().get().getDurationInMillis()));
        return builder.build();
    }
}

