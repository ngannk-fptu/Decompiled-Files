/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.confluence.api.model.backuprestore.JobOperation
 *  com.atlassian.confluence.api.model.backuprestore.JobScope
 *  com.atlassian.confluence.api.model.backuprestore.JobState
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.confluence.impl.backuprestore.analytics;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.api.model.backuprestore.JobOperation;
import com.atlassian.confluence.api.model.backuprestore.JobScope;
import com.atlassian.confluence.api.model.backuprestore.JobState;
import com.atlassian.event.api.AsynchronousPreferred;

@AsynchronousPreferred
@EventName(value="confluence.backup-restore.finish-job")
public class BackupRestoreFinishJobAnalyticsEvent {
    private final Long jobId;
    private final JobScope jobScope;
    private final JobOperation jobOperation;
    private final JobState jobState;
    private final Long objectsProcessed;
    private final Long backupZipSize;
    private final Long totalDuration;
    private final Boolean attachmentsIncluded;

    public BackupRestoreFinishJobAnalyticsEvent(Long jobId, JobScope jobScope, JobOperation jobOperation, JobState jobState, Long objectsProcessed, Long backupZipSize, Long totalDuration, Boolean attachmentsIncluded) {
        this.jobId = jobId;
        this.jobScope = jobScope;
        this.jobOperation = jobOperation;
        this.jobState = jobState;
        this.objectsProcessed = objectsProcessed;
        this.backupZipSize = backupZipSize;
        this.totalDuration = totalDuration;
        this.attachmentsIncluded = attachmentsIncluded;
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

    public JobState getJobState() {
        return this.jobState;
    }

    public Long getObjectsProcessed() {
        return this.objectsProcessed;
    }

    public Long getBackupZipSize() {
        return this.backupZipSize;
    }

    public Long getTotalDuration() {
        return this.totalDuration;
    }

    public Boolean getAttachmentsIncluded() {
        return this.attachmentsIncluded;
    }
}

