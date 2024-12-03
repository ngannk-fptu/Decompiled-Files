/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.restore.taskrunners;

import com.atlassian.confluence.backuprestore.BackupRestoreJob;
import com.atlassian.confluence.backuprestore.BackupRestoreSettings;
import com.atlassian.confluence.impl.backuprestore.converters.PublicSettingsToJsonConverter;
import com.atlassian.confluence.impl.backuprestore.dao.BackupRestoreJobDao;
import com.atlassian.confluence.impl.backuprestore.domain.BackupRestoreJobSettingsRecord;
import com.atlassian.confluence.impl.backuprestore.restore.HiLoGeneratorInitialiserOnSiteRestore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SiteRestoreJobResurrector {
    private static final Logger log = LoggerFactory.getLogger(SiteRestoreJobResurrector.class);
    private final PublicSettingsToJsonConverter converter = new PublicSettingsToJsonConverter();
    private final BackupRestoreJobDao backupRestoreJobDao;

    public SiteRestoreJobResurrector(BackupRestoreJobDao backupRestoreJobDao) {
        this.backupRestoreJobDao = backupRestoreJobDao;
    }

    public void resurrectSiteRestoreJob(BackupRestoreJob job, BackupRestoreSettings settings, HiLoGeneratorInitialiserOnSiteRestore hiLoGeneratorInitialiserOnSiteRestore) {
        log.debug("Resurrecting site restore job record with id {} (because it was removed when the whole database was dropped).", (Object)job.getId());
        this.backupRestoreJobDao.saveAndKeepId(job);
        BackupRestoreJobSettingsRecord settingsRecord = new BackupRestoreJobSettingsRecord(job.getId(), this.converter.apply(settings));
        this.backupRestoreJobDao.save(settingsRecord);
        hiLoGeneratorInitialiserOnSiteRestore.registerNewId(job.getId());
    }
}

