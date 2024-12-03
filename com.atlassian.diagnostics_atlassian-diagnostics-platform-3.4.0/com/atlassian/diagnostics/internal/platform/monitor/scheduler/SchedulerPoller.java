/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.config.Schedule
 *  com.atlassian.scheduler.config.Schedule$Type
 *  com.atlassian.scheduler.status.JobDetails
 *  com.atlassian.scheduler.status.RunDetails
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics.internal.platform.monitor.scheduler;

import com.atlassian.diagnostics.internal.platform.ConsecutiveAlertGate;
import com.atlassian.diagnostics.internal.platform.ConsecutiveAlertGateFactory;
import com.atlassian.diagnostics.internal.platform.monitor.scheduler.RunningJobDiagnostic;
import com.atlassian.diagnostics.internal.platform.monitor.scheduler.ScheduledJobDiagnostic;
import com.atlassian.diagnostics.internal.platform.monitor.scheduler.SchedulerDiagnostic;
import com.atlassian.diagnostics.internal.platform.monitor.scheduler.SchedulerDiagnosticProvider;
import com.atlassian.diagnostics.internal.platform.monitor.scheduler.SchedulerMonitor;
import com.atlassian.diagnostics.internal.platform.monitor.scheduler.SchedulerMonitorConfiguration;
import com.atlassian.diagnostics.internal.platform.poller.DiagnosticPoller;
import com.atlassian.scheduler.config.Schedule;
import com.atlassian.scheduler.status.JobDetails;
import com.atlassian.scheduler.status.RunDetails;
import java.time.Clock;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nonnull;

public class SchedulerPoller
extends DiagnosticPoller<SchedulerMonitorConfiguration> {
    private final SchedulerMonitor schedulerMonitor;
    private final SchedulerDiagnosticProvider diagnosticProvider;
    private final ConsecutiveAlertGate highUtilizationAlertGate;

    public SchedulerPoller(@Nonnull SchedulerMonitorConfiguration config, @Nonnull SchedulerMonitor schedulerMonitor, @Nonnull SchedulerDiagnosticProvider diagnosticProvider, @Nonnull Clock clock, @Nonnull ConsecutiveAlertGateFactory alertGateFactory) {
        super(SchedulerPoller.class.getName(), config);
        this.schedulerMonitor = schedulerMonitor;
        this.diagnosticProvider = diagnosticProvider;
        this.highUtilizationAlertGate = alertGateFactory.createAlertGate(config::highUtilizationTimeWindow, clock);
    }

    @Override
    protected void execute() {
        SchedulerDiagnostic diagnostic = this.diagnosticProvider.getDiagnostic();
        this.raiseAlertIfWorkerThreadsHighlyUtilized(diagnostic.getRunningJobs(), diagnostic.getWorkerThreads());
        this.raiseAlertIfJobExecutionExceededInterval(diagnostic.getScheduledJobs());
    }

    private void raiseAlertIfWorkerThreadsHighlyUtilized(List<RunningJobDiagnostic> runningJobs, int workerThreadCount) {
        if (this.highUtilizationAlertGate.shouldRaiseAlert(() -> runningJobs.size() >= workerThreadCount)) {
            this.schedulerMonitor.raiseAlertForHighUtilization(Instant.now(), runningJobs, workerThreadCount);
        }
    }

    private void raiseAlertIfJobExecutionExceededInterval(Collection<ScheduledJobDiagnostic> scheduledJobs) {
        scheduledJobs.forEach(scheduledJob -> scheduledJob.getLastRun().ifPresent(lastRun -> {
            if (this.lastRunDurationExceededInterval(scheduledJob.getJobDetails(), (RunDetails)lastRun)) {
                this.schedulerMonitor.raiseAlertForSlowJob(Instant.now(), (ScheduledJobDiagnostic)scheduledJob);
            }
        }));
    }

    private boolean lastRunDurationExceededInterval(JobDetails jobDetails, RunDetails lastRun) {
        Schedule schedule = jobDetails.getSchedule();
        return this.isIntervalSchedule(schedule) && this.isLastRunDurationLongerThanScheduledInterval(schedule, lastRun);
    }

    private boolean isIntervalSchedule(Schedule schedule) {
        return Schedule.Type.INTERVAL == schedule.getType();
    }

    private boolean isLastRunDurationLongerThanScheduledInterval(Schedule schedule, RunDetails lastRun) {
        return schedule.getIntervalScheduleInfo().getIntervalInMillis() > 0L && lastRun.getDurationInMillis() >= schedule.getIntervalScheduleInfo().getIntervalInMillis();
    }
}

