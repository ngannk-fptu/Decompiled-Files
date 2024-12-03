/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.impl.backuprestore.scheduledjob;

import com.atlassian.confluence.impl.backuprestore.ConfluenceBackupRestoreManager;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import javax.annotation.Nullable;

public class BackupRestoreScheduledJob
implements JobRunner {
    private final ConfluenceBackupRestoreManager confluenceBackupRestoreManager;

    public BackupRestoreScheduledJob(ConfluenceBackupRestoreManager confluenceBackupRestoreManager) {
        this.confluenceBackupRestoreManager = confluenceBackupRestoreManager;
    }

    @Nullable
    public JobRunnerResponse runJob(JobRunnerRequest jobRunnerRequest) {
        this.confluenceBackupRestoreManager.processJobsFromTheQueue();
        return JobRunnerResponse.success();
    }
}

