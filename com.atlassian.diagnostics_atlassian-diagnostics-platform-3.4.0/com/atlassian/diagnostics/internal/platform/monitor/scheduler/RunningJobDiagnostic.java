/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.core.RunningJob
 */
package com.atlassian.diagnostics.internal.platform.monitor.scheduler;

import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.core.RunningJob;
import java.util.Optional;

public class RunningJobDiagnostic {
    private final RunningJob runningJob;
    private final Optional<JobRunner> jobRunner;

    public RunningJobDiagnostic(RunningJob runningJob, Optional<JobRunner> jobRunner) {
        this.runningJob = runningJob;
        this.jobRunner = jobRunner;
    }

    public RunningJob getRunningJob() {
        return this.runningJob;
    }

    public Optional<JobRunner> getJobRunner() {
        return this.jobRunner;
    }
}

