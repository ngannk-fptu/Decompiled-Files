/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.backuprestore.JobOperation
 *  com.atlassian.confluence.api.model.backuprestore.JobScope
 *  com.atlassian.confluence.api.model.backuprestore.JobState
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.backup;

import com.atlassian.confluence.api.model.backuprestore.JobOperation;
import com.atlassian.confluence.api.model.backuprestore.JobScope;
import com.atlassian.confluence.api.model.backuprestore.JobState;
import com.atlassian.confluence.backuprestore.BackupRestoreJob;
import com.atlassian.confluence.backuprestore.BackupRestoreSettings;
import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.ParallelTasksExecutor;
import com.atlassian.confluence.impl.backuprestore.backup.BackupDescriptorWriter;
import com.atlassian.confluence.impl.backuprestore.backup.container.BackupContainerWriter;
import com.atlassian.confluence.impl.backuprestore.dao.BackupRestoreJobDao;
import com.atlassian.confluence.impl.backuprestore.statistics.JobStatisticsInfo;
import com.atlassian.confluence.importexport.impl.ExportScope;
import java.io.File;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractBackupService {
    private static final Logger log = LoggerFactory.getLogger(AbstractBackupService.class);
    final BackupRestoreJobDao backupRestoreJobDao;
    public static final int DELETE_TEMP_BACKUPS_OLDER_THAN_HOURS = Integer.getInteger("confluence.backuprestore.backup.ttl-in-hours", 72);

    public AbstractBackupService(BackupRestoreJobDao backupRestoreJobDao) {
        this.backupRestoreJobDao = backupRestoreJobDao;
    }

    public abstract void doBackupSynchronously(BackupRestoreJob var1, BackupRestoreSettings var2) throws BackupRestoreException, InterruptedException;

    protected void validateBackupJob(BackupRestoreJob job, BackupRestoreSettings settings, JobScope expectedJobScope) {
        if (!JobOperation.BACKUP.equals((Object)job.getJobOperation())) {
            throw new IllegalStateException(String.format("Invalid job operation. Expected %s, but received %s", JobOperation.BACKUP, job.getJobScope()));
        }
        if (!expectedJobScope.equals((Object)job.getJobScope())) {
            throw new IllegalStateException(String.format("Invalid job scope. Expected %s, but received %s", expectedJobScope, job.getJobScope()));
        }
        if (settings.isSkipAuditRecordsExport()) {
            throw new IllegalStateException("skipAuditRecordsExport is not supported in this version");
        }
        if (settings.isSkipHistoricalVersions()) {
            throw new IllegalStateException("skipHistoricalVersions is not supported in this version");
        }
        if (!settings.getNotificationEmails().isEmpty()) {
            throw new IllegalStateException("Notifications by emails are not supported in this version");
        }
    }

    protected File performBackup(BackupRestoreJob job, BackupRestoreSettings settings, ParallelTasksExecutor parallelTasksExecutor, BackupContainerWriter containerWriter) throws ExecutionException, InterruptedException, TimeoutException, BackupRestoreException {
        File outputFile;
        try {
            log.info("{} backup [{}] is now backing up entities and attachments.", (Object)job.getJobScope(), (Object)job.getId());
            JobStatisticsInfo jobStatisticsInfo = this.backupAllEntitiesAndAttachments(job, settings, parallelTasksExecutor, containerWriter);
            BackupRestoreJob jobToUpdate = this.backupRestoreJobDao.getById(job.getId());
            jobToUpdate.setJobState(JobState.COMPLETING);
            this.backupRestoreJobDao.updateInNewTransaction(jobToUpdate);
            log.info("{} backup [{}] is now writing the exportDescriptor.", (Object)job.getJobScope(), (Object)job.getId());
            this.writeDescriptionProperties(settings, containerWriter, settings.getSpaceKeys(), jobStatisticsInfo);
            outputFile = containerWriter.getOutputFile();
        }
        catch (Exception e) {
            log.debug("Start interrupting all tasks");
            parallelTasksExecutor.interruptAllJobs();
            throw e;
        }
        return outputFile;
    }

    protected abstract JobStatisticsInfo backupAllEntitiesAndAttachments(BackupRestoreJob var1, BackupRestoreSettings var2, ParallelTasksExecutor var3, BackupContainerWriter var4) throws ExecutionException, InterruptedException, TimeoutException, BackupRestoreException;

    private void writeDescriptionProperties(BackupRestoreSettings backupRestoreSettings, BackupContainerWriter containerWriter, Collection<String> spaceKeys, JobStatisticsInfo jobStatisticsInfo) throws BackupRestoreException {
        BackupDescriptorWriter backupDescriptorWriter = new BackupDescriptorWriter();
        backupDescriptorWriter.writeBackupDescriptor(containerWriter, backupRestoreSettings.getJobScope() == JobScope.SPACE ? ExportScope.SPACE : ExportScope.SITE, !backupRestoreSettings.isSkipAttachments(), spaceKeys, jobStatisticsInfo.getPersistedObjectsCount());
    }

    protected BackupRestoreJob postBackupJobUpdate(String newFileName, BackupRestoreSettings settings, long jobId, ParallelTasksExecutor parallelTasksExecutor) throws ExecutionException, InterruptedException {
        Callable<BackupRestoreJob> task = () -> {
            BackupRestoreJob currentJob = this.backupRestoreJobDao.getById(jobId);
            if (!settings.isKeepPermanently()) {
                Instant backupTtl = Instant.now().plus((long)DELETE_TEMP_BACKUPS_OLDER_THAN_HOURS, ChronoUnit.HOURS);
                currentJob.setFileDeleteTime(backupTtl);
            }
            currentJob.setFileName(newFileName);
            currentJob.setFileExists(true);
            this.backupRestoreJobDao.update(currentJob);
            return currentJob;
        };
        return parallelTasksExecutor.runTaskAsync(task, "Post backup job update").get();
    }
}

