/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunnerRequest
 */
package com.atlassian.scheduler.core;

import com.atlassian.scheduler.JobRunnerRequest;

public interface RunningJob
extends JobRunnerRequest {
    public void cancel();
}

