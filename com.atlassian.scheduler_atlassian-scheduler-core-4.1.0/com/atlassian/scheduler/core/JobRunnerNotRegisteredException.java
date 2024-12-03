/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.JobRunnerKey
 */
package com.atlassian.scheduler.core;

import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobRunnerKey;

public class JobRunnerNotRegisteredException
extends SchedulerServiceException {
    private static final long serialVersionUID = 1L;
    private final JobRunnerKey jobRunnerKey;

    public JobRunnerNotRegisteredException(JobRunnerKey jobRunnerKey) {
        super("No job runner registered for job runner key '" + jobRunnerKey + '\'');
        this.jobRunnerKey = jobRunnerKey;
    }

    public JobRunnerKey getJobRunnerKey() {
        return this.jobRunnerKey;
    }
}

