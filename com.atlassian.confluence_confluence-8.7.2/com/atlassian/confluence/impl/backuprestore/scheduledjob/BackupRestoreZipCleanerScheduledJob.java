/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.scheduledjob;

import com.atlassian.confluence.backuprestore.BackupRestoreJob;
import com.atlassian.confluence.impl.backuprestore.BackupRestoreFilesystemManager;
import com.atlassian.confluence.impl.backuprestore.dao.BackupRestoreJobDao;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import java.io.IOException;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackupRestoreZipCleanerScheduledJob
implements JobRunner {
    private static final Logger log = LoggerFactory.getLogger(BackupRestoreZipCleanerScheduledJob.class);
    private final BackupRestoreJobDao backupRestoreJobDao;
    private final BackupRestoreFilesystemManager backupRestoreFilesystemManager;

    public BackupRestoreZipCleanerScheduledJob(BackupRestoreJobDao backupRestoreJobDao, BackupRestoreFilesystemManager backupRestoreFilesystemManager) {
        this.backupRestoreJobDao = backupRestoreJobDao;
        this.backupRestoreFilesystemManager = backupRestoreFilesystemManager;
    }

    @Nullable
    public JobRunnerResponse runJob(JobRunnerRequest jobRunnerRequest) {
        this.backupRestoreJobDao.findJobsWithExpiredZips().forEach(this::deleteZip);
        return JobRunnerResponse.success();
    }

    private void deleteZip(BackupRestoreJob job) {
        try {
            this.backupRestoreFilesystemManager.deleteZipFile(job.getFileName(), job.getJobScope());
            job.setFileExists(false);
            this.backupRestoreJobDao.update(job);
        }
        catch (IOException e) {
            log.warn("Was unable to cleanup {} backup zip: {} from the restore folder", new Object[]{job.getJobScope(), job.getFileName(), e});
        }
    }
}

