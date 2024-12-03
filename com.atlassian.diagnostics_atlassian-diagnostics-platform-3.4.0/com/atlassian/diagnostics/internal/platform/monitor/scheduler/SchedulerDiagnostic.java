/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.diagnostics.internal.platform.monitor.scheduler;

import com.atlassian.diagnostics.internal.platform.monitor.scheduler.RunningJobDiagnostic;
import com.atlassian.diagnostics.internal.platform.monitor.scheduler.ScheduledJobDiagnostic;
import java.util.List;

public class SchedulerDiagnostic {
    private final int workerThreads;
    private final List<RunningJobDiagnostic> runningJobs;
    private final List<ScheduledJobDiagnostic> scheduledJobs;

    public SchedulerDiagnostic(int workerThreads, List<RunningJobDiagnostic> runningJobs, List<ScheduledJobDiagnostic> scheduledJobs) {
        this.workerThreads = workerThreads;
        this.runningJobs = runningJobs;
        this.scheduledJobs = scheduledJobs;
    }

    public int getWorkerThreads() {
        return this.workerThreads;
    }

    public List<RunningJobDiagnostic> getRunningJobs() {
        return this.runningJobs;
    }

    public List<ScheduledJobDiagnostic> getScheduledJobs() {
        return this.scheduledJobs;
    }
}

