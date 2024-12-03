/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.confluence.backuprestore;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.backuprestore.BackupRestoreJob;
import com.atlassian.confluence.backuprestore.BackupRestoreJobResult;
import com.atlassian.confluence.backuprestore.BackupRestoreJobsSearchFilter;
import com.atlassian.confluence.backuprestore.BackupRestoreSettings;
import com.atlassian.confluence.backuprestore.exception.NotPermittedException;
import com.atlassian.confluence.backuprestore.exception.TheSameSpaceBackupRestoreJobAlreadyInProgressException;
import java.util.Collection;
import java.util.Optional;

@ExperimentalApi
public interface BackupRestoreManager {
    public Optional<BackupRestoreJob> getJob(Long var1) throws NotPermittedException;

    public BackupRestoreJob startSpaceBackup(BackupRestoreSettings var1) throws NotPermittedException, TheSameSpaceBackupRestoreJobAlreadyInProgressException;

    public BackupRestoreJob startSiteBackup(BackupRestoreSettings var1) throws NotPermittedException;

    public BackupRestoreJob startSpaceRestore(BackupRestoreSettings var1) throws NotPermittedException;

    public BackupRestoreJob startSiteRestore(BackupRestoreSettings var1) throws NotPermittedException;

    public Optional<BackupRestoreJob> cancelJob(Long var1) throws NotPermittedException;

    public int cancelAllJobsFromQueue() throws NotPermittedException;

    public Collection<BackupRestoreJob> findJobs(BackupRestoreJobsSearchFilter var1);

    public void assertUserHasSystemAdminPermissions() throws NotPermittedException;

    public BackupRestoreSettings getSettingsById(long var1);

    public Optional<BackupRestoreJobResult> getStatisticsById(long var1);
}

