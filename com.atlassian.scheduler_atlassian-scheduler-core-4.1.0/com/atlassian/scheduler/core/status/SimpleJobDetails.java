/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.RunMode
 *  com.atlassian.scheduler.config.Schedule
 *  com.atlassian.scheduler.util.Safe
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.scheduler.core.status;

import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;
import com.atlassian.scheduler.core.status.AbstractJobDetails;
import com.atlassian.scheduler.util.Safe;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class SimpleJobDetails
extends AbstractJobDetails {
    private final Map<String, Serializable> parameters;

    public SimpleJobDetails(JobId jobId, JobRunnerKey jobRunnerKey, RunMode runMode, Schedule schedule, @Nullable Date nextRunTime, byte[] rawParameters, @Nullable Map<String, Serializable> parameters) {
        super(jobId, jobRunnerKey, runMode, schedule, nextRunTime, rawParameters);
        this.parameters = Safe.copy(parameters);
    }

    @Nonnull
    public Map<String, Serializable> getParameters() {
        return this.parameters;
    }

    public boolean isRunnable() {
        return true;
    }

    @Override
    protected void appendToStringDetails(StringBuilder sb) {
        sb.append(",parameters=").append(this.parameters);
    }
}

