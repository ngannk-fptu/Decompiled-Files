/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.backuprestore.JobScope
 *  com.atlassian.event.api.EventPublisher
 */
package com.atlassian.confluence.impl.backuprestore.restore.events;

import com.atlassian.confluence.api.model.backuprestore.JobScope;
import com.atlassian.confluence.backuprestore.BackupRestoreJob;
import com.atlassian.confluence.backuprestore.BackupRestoreSettings;
import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.event.events.admin.AsyncImportFinishedEvent;
import com.atlassian.confluence.event.events.admin.AsyncImportStartedEvent;
import com.atlassian.confluence.event.events.admin.ImportFinishedEvent;
import com.atlassian.confluence.impl.backuprestore.restore.confluencelocker.ConfluenceLockerOnSiteRestore;
import com.atlassian.confluence.impl.backuprestore.restore.confluencelocker.events.RestoreFailedEvent;
import com.atlassian.confluence.impl.backuprestore.restore.confluencelocker.events.RestoreInProgressEvent;
import com.atlassian.confluence.impl.backuprestore.restore.confluencelocker.events.RestoreLockDatabaseEvent;
import com.atlassian.confluence.impl.backuprestore.restore.confluencelocker.events.RestoreSucceededEvent;
import com.atlassian.confluence.impl.backuprestore.restore.confluencelocker.events.RestoreUnlockDatabaseEvent;
import com.atlassian.confluence.impl.backuprestore.restore.dao.RestoreDao;
import com.atlassian.confluence.importexport.DefaultImportContext;
import com.atlassian.confluence.importexport.ImportContext;
import com.atlassian.confluence.importexport.ImportExportException;
import com.atlassian.confluence.importexport.impl.ExportDescriptor;
import com.atlassian.confluence.importexport.impl.UnexpectedImportZipFileContents;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.event.api.EventPublisher;
import java.util.Collection;
import java.util.Properties;

public class OnRestoreEventsSender {
    private final EventPublisher eventPublisher;
    private final UserAccessor userAccessor;
    private final RestoreDao restoreDao;

    public OnRestoreEventsSender(EventPublisher eventPublisher, UserAccessor userAccessor, RestoreDao restoreDao) {
        this.eventPublisher = eventPublisher;
        this.userAccessor = userAccessor;
        this.restoreDao = restoreDao;
    }

    public void sendStartEvents(BackupRestoreJob job, BackupRestoreSettings settings, Collection<String> spaceKeys, Properties properties) throws BackupRestoreException {
        try {
            if (job.getJobScope().equals((Object)JobScope.SITE)) {
                this.sendInProgressEvent(job.getJobScope(), 0L, 0L, false, true);
                this.sendStartSiteImportEvents(job, settings, properties);
            } else {
                this.sendStartSpaceImportEvents(job, settings, spaceKeys, properties);
            }
        }
        catch (ImportExportException | UnexpectedImportZipFileContents e) {
            throw new BackupRestoreException("Unable to send events: " + e.getMessage(), e);
        }
    }

    public void sendFinishEvents(BackupRestoreJob job, BackupRestoreSettings settings, Collection<String> spaceKeys, Properties properties) throws BackupRestoreException {
        try {
            if (job.getJobScope().equals((Object)JobScope.SITE)) {
                this.sendFinishSiteImportEvents(settings, properties);
            } else {
                this.sendFinishSpaceImportEvents(job, settings, spaceKeys, properties);
            }
        }
        catch (ImportExportException | UnexpectedImportZipFileContents e) {
            throw new BackupRestoreException("Unable to send events: " + e.getMessage(), e);
        }
        this.sendSuccessfulEvent(job);
    }

    public void sendInProgressEvent(JobScope jobScope, long processedObjects, long totalNumberOfObjects, boolean databaseLocked, boolean isDisplayJohnson) {
        RestoreInProgressEvent event = new RestoreInProgressEvent(this, jobScope, processedObjects, totalNumberOfObjects, databaseLocked, isDisplayJohnson);
        this.eventPublisher.publish((Object)event);
    }

    public void sendLockDatabaseEvent() {
        this.eventPublisher.publish((Object)new RestoreLockDatabaseEvent(this, JobScope.SITE));
    }

    public void sendUnlockDatabaseEvent() {
        this.eventPublisher.publish((Object)new RestoreUnlockDatabaseEvent(this, JobScope.SITE));
    }

    public void sendSuccessfulEvent(BackupRestoreJob job) {
        this.eventPublisher.publish((Object)new RestoreSucceededEvent(this, job.getJobScope()));
    }

    public void sendFailureEvent(BackupRestoreJob job, String errorMessage) {
        this.eventPublisher.publish((Object)new RestoreFailedEvent(this, job.getJobScope(), errorMessage, ConfluenceLockerOnSiteRestore.isDisplayJohnson()));
    }

    private void sendStartSiteImportEvents(BackupRestoreJob job, BackupRestoreSettings settings, Properties properties) throws ImportExportException, UnexpectedImportZipFileContents {
        ExportDescriptor exportDescriptor = new ExportDescriptor(properties);
        ImportContext context = this.createImportContextForEvents(null, settings.getFileName(), exportDescriptor, job.getOwner(), settings.isSkipReindex());
        this.eventPublisher.publish((Object)new AsyncImportStartedEvent(this, context));
    }

    private void sendStartSpaceImportEvents(BackupRestoreJob job, BackupRestoreSettings settings, Collection<String> spaceKeys, Properties properties) throws ImportExportException, UnexpectedImportZipFileContents {
        ExportDescriptor exportDescriptor = new ExportDescriptor(properties);
        spaceKeys.forEach(spaceKey -> {
            ImportContext context = this.createImportContextForEvents((String)spaceKey, settings.getFileName(), exportDescriptor, job.getOwner(), settings.isSkipReindex());
            this.eventPublisher.publish((Object)new AsyncImportStartedEvent(this, context));
        });
    }

    private void sendFinishSiteImportEvents(BackupRestoreSettings settings, Properties properties) throws ImportExportException, UnexpectedImportZipFileContents {
        ExportDescriptor exportDescriptor = new ExportDescriptor(properties);
        ImportContext context = this.createImportContextForEvents(null, settings.getFileName(), exportDescriptor, null, settings.isSkipReindex());
        this.restoreDao.doInTransaction(tx -> {
            this.eventPublisher.publish((Object)new ImportFinishedEvent(this, context));
            return null;
        });
        this.eventPublisher.publish((Object)new AsyncImportFinishedEvent(this, context));
    }

    private void sendFinishSpaceImportEvents(BackupRestoreJob job, BackupRestoreSettings settings, Collection<String> spaceKeys, Properties properties) throws ImportExportException, UnexpectedImportZipFileContents {
        ExportDescriptor exportDescriptor = new ExportDescriptor(properties);
        spaceKeys.forEach(spaceKey -> {
            ImportContext context = this.createImportContextForEvents((String)spaceKey, settings.getFileName(), exportDescriptor, job.getOwner(), settings.isSkipReindex());
            this.restoreDao.doInTransaction(tx -> {
                this.eventPublisher.publish((Object)new ImportFinishedEvent(this, context));
                return null;
            });
            this.eventPublisher.publish((Object)new AsyncImportFinishedEvent(this, context));
        });
    }

    private ImportContext createImportContextForEvents(String spaceKey, String file, ExportDescriptor exportDescriptor, String userName, boolean isSkipReindex) {
        ConfluenceUser user = userName != null ? this.userAccessor.getUserByName(userName) : null;
        DefaultImportContext importContext = new DefaultImportContext(file, exportDescriptor, user);
        importContext.setDefaultSpaceKey(spaceKey);
        importContext.setSpaceKeyOfSpaceImport(spaceKey);
        importContext.setIncrementalImport(spaceKey != null);
        importContext.setDefaultUsersGroup(exportDescriptor.getDefaultUserGroup());
        importContext.setRebuildIndex(!isSkipReindex);
        return importContext;
    }
}

