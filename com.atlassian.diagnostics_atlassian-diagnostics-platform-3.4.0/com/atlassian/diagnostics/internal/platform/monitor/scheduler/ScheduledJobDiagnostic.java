/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.status.JobDetails
 *  com.atlassian.scheduler.status.RunDetails
 */
package com.atlassian.diagnostics.internal.platform.monitor.scheduler;

import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.status.JobDetails;
import com.atlassian.scheduler.status.RunDetails;
import java.util.Optional;

public class ScheduledJobDiagnostic {
    private final JobDetails jobDetails;
    private final Optional<RunDetails> lastRun;
    private final Optional<JobRunner> jobRunner;

    public ScheduledJobDiagnostic(JobDetails jobDetails, Optional<RunDetails> lastRun, Optional<JobRunner> jobRunner) {
        this.jobDetails = jobDetails;
        this.lastRun = lastRun;
        this.jobRunner = jobRunner;
    }

    public JobDetails getJobDetails() {
        return this.jobDetails;
    }

    public Optional<RunDetails> getLastRun() {
        return this.lastRun;
    }

    public Optional<JobRunner> getJobRunner() {
        return this.jobRunner;
    }
}

