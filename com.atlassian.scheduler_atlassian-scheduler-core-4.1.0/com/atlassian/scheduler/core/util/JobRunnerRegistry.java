/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nonnull
 */
package com.atlassian.scheduler.core.util;

import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.google.common.collect.ImmutableSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.annotation.Nonnull;

public class JobRunnerRegistry {
    private final ConcurrentMap<JobRunnerKey, JobRunner> jobRunnerRegistry = new ConcurrentHashMap<JobRunnerKey, JobRunner>();

    public void registerJobRunner(JobRunnerKey jobRunnerKey, JobRunner jobRunner) {
        this.jobRunnerRegistry.put(Objects.requireNonNull(jobRunnerKey, "jobRunnerKey"), Objects.requireNonNull(jobRunner, "jobRunner"));
    }

    public void unregisterJobRunner(JobRunnerKey jobRunnerKey) {
        this.jobRunnerRegistry.remove(Objects.requireNonNull(jobRunnerKey, "jobRunnerKey"));
    }

    public JobRunner getJobRunner(JobRunnerKey jobRunnerKey) {
        return (JobRunner)this.jobRunnerRegistry.get(Objects.requireNonNull(jobRunnerKey, "jobRunnerKey"));
    }

    @Nonnull
    public Set<JobRunnerKey> getRegisteredJobRunnerKeys() {
        return ImmutableSet.copyOf(this.jobRunnerRegistry.keySet());
    }
}

