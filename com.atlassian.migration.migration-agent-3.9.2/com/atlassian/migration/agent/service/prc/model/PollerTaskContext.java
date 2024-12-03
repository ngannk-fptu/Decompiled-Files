/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.migration.prc.client.PrcPollTask
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.prc.model;

import com.atlassian.migration.prc.client.PrcPollTask;
import lombok.Generated;

public class PollerTaskContext {
    private final Long jobStartTime;
    private final PrcPollTask prcPollTask;

    @Generated
    public PollerTaskContext(Long jobStartTime, PrcPollTask prcPollTask) {
        this.jobStartTime = jobStartTime;
        this.prcPollTask = prcPollTask;
    }

    @Generated
    public Long getJobStartTime() {
        return this.jobStartTime;
    }

    @Generated
    public PrcPollTask getPrcPollTask() {
        return this.prcPollTask;
    }
}

