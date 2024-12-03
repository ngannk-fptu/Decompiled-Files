/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.backuprestore.JobOperation
 *  com.atlassian.confluence.api.model.backuprestore.JobScope
 *  com.atlassian.confluence.api.model.backuprestore.JobState
 */
package com.atlassian.confluence.impl.backuprestore;

import com.atlassian.confluence.api.model.backuprestore.JobOperation;
import com.atlassian.confluence.api.model.backuprestore.JobScope;
import com.atlassian.confluence.api.model.backuprestore.JobState;
import com.atlassian.confluence.backuprestore.BackupRestoreJob;
import com.atlassian.confluence.backuprestore.BackupRestoreSettings;
import com.atlassian.confluence.impl.backuprestore.converters.JsonToPublicSettingsConverter;
import com.atlassian.confluence.impl.backuprestore.converters.PublicSettingsToJsonConverter;
import com.atlassian.confluence.impl.backuprestore.dao.BackupRestoreJobDao;
import com.atlassian.confluence.impl.backuprestore.domain.BackupRestoreJobSettingsRecord;
import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class IndependentBackupRestoreJobManager {
    private final BackupRestoreJobDao backupRestoreJobDao;
    private final JsonToPublicSettingsConverter jsonToPublicSettingsConverter = new JsonToPublicSettingsConverter();
    private final PublicSettingsToJsonConverter publicSettingsToJsonConverter = new PublicSettingsToJsonConverter();

    public IndependentBackupRestoreJobManager(BackupRestoreJobDao backupRestoreJobDao) {
        this.backupRestoreJobDao = backupRestoreJobDao;
    }

    public BackupRestoreSettings getSettingsById(ExecutorService executorService, long jobId) {
        Future<BackupRestoreSettings> future = executorService.submit(() -> {
            BackupRestoreJobSettingsRecord settingsRecord = this.backupRestoreJobDao.getSettingsById(jobId);
            return this.jsonToPublicSettingsConverter.apply(settingsRecord.getSettings());
        });
        return this.unwrapFuture(future);
    }

    public BackupRestoreJob createAndSaveNewJob(ExecutorService executorService, JobOperation jobOperation, JobScope jobScope, JobState jobState, Instant createTime, String owner, BackupRestoreSettings backupRestoreSettings) {
        Future<BackupRestoreJob> future = executorService.submit(() -> {
            BackupRestoreJob job = new BackupRestoreJob();
            job.setJobOperation(jobOperation);
            job.setJobScope(jobScope);
            job.setCreateTime(createTime);
            job.setOwner(owner);
            job.setJobState(jobState);
            job.setFileName(backupRestoreSettings.getFileName());
            job.addSpaceKeys(backupRestoreSettings.getSpaceKeys());
            if (JobOperation.BACKUP.equals((Object)jobOperation)) {
                job.setFileExists(false);
            }
            this.backupRestoreJobDao.save(job);
            return job;
        });
        return this.unwrapFuture(future);
    }

    public BackupRestoreSettings createAndSaveNewJobSettingsRecord(ExecutorService executorService, long jobId, BackupRestoreSettings backupRestoreSettings) {
        Future<BackupRestoreSettings> future = executorService.submit(() -> {
            BackupRestoreJobSettingsRecord backupRestoreJobSettingsRecord = new BackupRestoreJobSettingsRecord(jobId, this.publicSettingsToJsonConverter.apply(backupRestoreSettings));
            this.backupRestoreJobDao.save(backupRestoreJobSettingsRecord);
            return backupRestoreSettings;
        });
        return this.unwrapFuture(future);
    }

    private <T> T unwrapFuture(Future<T> future) {
        try {
            return future.get();
        }
        catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}

