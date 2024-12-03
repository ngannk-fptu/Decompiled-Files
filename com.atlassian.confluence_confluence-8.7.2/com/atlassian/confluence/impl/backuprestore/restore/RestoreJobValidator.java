/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.backuprestore.JobOperation
 *  com.atlassian.confluence.api.model.backuprestore.JobScope
 */
package com.atlassian.confluence.impl.backuprestore.restore;

import com.atlassian.confluence.api.model.backuprestore.JobOperation;
import com.atlassian.confluence.api.model.backuprestore.JobScope;
import com.atlassian.confluence.backuprestore.BackupRestoreJob;
import com.atlassian.confluence.backuprestore.BackupRestoreSettings;

public class RestoreJobValidator {
    public void validateSpaceRestoreJob(BackupRestoreJob job, BackupRestoreSettings settings) {
        this.validateJobScope(job, JobScope.SPACE);
        this.validateRestoreJob(job, settings);
    }

    public void validateSiteRestoreJob(BackupRestoreJob job, BackupRestoreSettings settings) {
        this.validateJobScope(job, JobScope.SITE);
        this.validateRestoreJob(job, settings);
    }

    private void validateJobScope(BackupRestoreJob job, JobScope expectedScope) {
        if (!expectedScope.equals((Object)job.getJobScope())) {
            throw new IllegalArgumentException(String.format("Job has an incorrect scope. Expected %s, but received %s", expectedScope, job.getJobScope()));
        }
    }

    private void validateRestoreJob(BackupRestoreJob job, BackupRestoreSettings settings) {
        if (!JobOperation.RESTORE.equals((Object)job.getJobOperation())) {
            throw new IllegalArgumentException(String.format("Job has an incorrect operation. Expected %s, but received %s", JobOperation.RESTORE, job.getJobOperation()));
        }
        if (settings.isSkipHistoricalVersions()) {
            throw new UnsupportedOperationException("skipHistoricalVersions is not supported in this version");
        }
        if (!settings.getNotificationEmails().isEmpty()) {
            throw new UnsupportedOperationException("Notifications by emails are not supported in this version");
        }
        if (!settings.getSpaceKeys().isEmpty()) {
            throw new IllegalArgumentException("XML space/site restore does not allow to provide space keys, but received " + settings.getSpaceKeys().size() + " keys");
        }
    }
}

