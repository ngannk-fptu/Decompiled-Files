/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.confluence.api.service.retention;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.retention.SoftCleanupJobStatus;

@ExperimentalApi
public interface SoftCleanupStatusService {
    public SoftCleanupJobStatus getCurrentStatus();

    public void setCurrentStatus(SoftCleanupJobStatus var1);
}

