/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.confluence.api.model.backuprestore.JobOperation
 *  com.atlassian.confluence.api.model.backuprestore.JobScope
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.confluence.impl.backuprestore.analytics;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.api.model.backuprestore.JobOperation;
import com.atlassian.confluence.api.model.backuprestore.JobScope;
import com.atlassian.event.api.AsynchronousPreferred;

@AsynchronousPreferred
@EventName(value="confluence.backup-restore.start-job")
public class BackupRestoreStartJobAnalyticsEvent {
    private final Long jobId;
    private final JobScope jobScope;
    private final JobOperation jobOperation;

    public BackupRestoreStartJobAnalyticsEvent(Long jobId, JobScope jobScope, JobOperation jobOperation) {
        this.jobId = jobId;
        this.jobScope = jobScope;
        this.jobOperation = jobOperation;
    }

    public Long getJobId() {
        return this.jobId;
    }

    public JobScope getJobScope() {
        return this.jobScope;
    }

    public JobOperation getJobOperation() {
        return this.jobOperation;
    }
}

