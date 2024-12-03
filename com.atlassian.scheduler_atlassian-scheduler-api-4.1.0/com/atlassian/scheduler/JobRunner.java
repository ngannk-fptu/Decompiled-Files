/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicSpi
 *  javax.annotation.Nullable
 */
package com.atlassian.scheduler;

import com.atlassian.annotations.PublicSpi;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import javax.annotation.Nullable;

@PublicSpi
public interface JobRunner {
    @Nullable
    public JobRunnerResponse runJob(JobRunnerRequest var1);
}

