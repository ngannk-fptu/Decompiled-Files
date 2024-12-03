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
import com.atlassian.confluence.backuprestore.BackupRestoreJobsSearchFilter;
import com.atlassian.confluence.impl.backuprestore.BackupRestoreFilesystemManager;
import com.atlassian.confluence.impl.backuprestore.backup.AbstractBackupService;
import com.atlassian.confluence.impl.backuprestore.dao.BackupRestoreJobDao;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackupRestoreJobCleanerScheduledJob
implements JobRunner {
    private static final Logger log = LoggerFactory.getLogger(BackupRestoreJobCleanerScheduledJob.class);
    public static final int BACKUP_RESTORE_JOBS_TTL_DAYS = Integer.getInteger("confluence.backuprestore.jobs.ttl-in-days", 14);
    private final BackupRestoreJobDao backupRestoreJobDao;
    private final BackupRestoreFilesystemManager backupRestoreFilesystemManager;

    public BackupRestoreJobCleanerScheduledJob(BackupRestoreJobDao backupRestoreJobDao, BackupRestoreFilesystemManager backupRestoreFilesystemManager) {
        this.backupRestoreJobDao = backupRestoreJobDao;
        this.backupRestoreFilesystemManager = backupRestoreFilesystemManager;
        if (BACKUP_RESTORE_JOBS_TTL_DAYS * 24 <= AbstractBackupService.DELETE_TEMP_BACKUPS_OLDER_THAN_HOURS) {
            log.warn("System property confluence.backuprestore.jobs.ttl-in-days ({} days) is shorter or equal to confluence.backuprestore.backup.ttl-in-hours ({} hours). Non-permanent backup files will be deleted on backup/restore job clean up.", (Object)BACKUP_RESTORE_JOBS_TTL_DAYS, (Object)AbstractBackupService.DELETE_TEMP_BACKUPS_OLDER_THAN_HOURS);
        }
    }

    @Nullable
    public JobRunnerResponse runJob(JobRunnerRequest jobRunnerRequest) {
        BackupRestoreJobsSearchFilter filter = new BackupRestoreJobsSearchFilter.Builder().dateRange(null, Instant.now().minus(Duration.ofDays(BACKUP_RESTORE_JOBS_TTL_DAYS))).build();
        try {
            for (BackupRestoreJob backupRestoreJob : this.backupRestoreJobDao.findJobs(filter)) {
                this.deleteJob(backupRestoreJob);
            }
        }
        catch (Exception e) {
            String msg = "Was unable to cleanup backup/restore jobs.";
            log.warn(msg, (Throwable)e);
            return JobRunnerResponse.failed((String)msg);
        }
        return JobRunnerResponse.success();
    }

    private void deleteJob(BackupRestoreJob job) throws IOException {
        if (Boolean.TRUE.equals(job.isFileExists()) && job.getFileDeleteTime() != null) {
            this.backupRestoreFilesystemManager.deleteZipFile(job.getFileName(), job.getJobScope());
            log.info("Deleted non-permanent backup file ({}) on job deletion. Please adjust confluence.backuprestore.jobs.ttl-in-days and confluence.backuprestore.backup.ttl-in-hours system properties if this is not intended.", (Object)job.getFileName());
        }
        this.backupRestoreJobDao.delete(job.getId());
    }
}

