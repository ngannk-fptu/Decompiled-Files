/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.johnson.event.Event
 *  com.atlassian.spring.container.ContainerManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.TransactionStatus
 *  org.springframework.transaction.support.TransactionCallback
 *  org.springframework.transaction.support.TransactionCallbackWithoutResult
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.confluence.util.longrunning;

import com.atlassian.confluence.internal.health.JohnsonEventLevel;
import com.atlassian.confluence.internal.health.JohnsonEventType;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDao;
import com.atlassian.confluence.setup.johnson.JohnsonUtils;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.util.longrunning.ConfluenceAbstractLongRunningTask;
import com.atlassian.johnson.event.Event;
import com.atlassian.spring.container.ContainerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

public class AttachmentMigrationLongRunningTask
extends ConfluenceAbstractLongRunningTask {
    private static final Logger log = LoggerFactory.getLogger(AttachmentMigrationLongRunningTask.class);
    protected AttachmentDao.AttachmentMigrator migrator;
    private String destinationStore;

    public AttachmentMigrationLongRunningTask(String destinationStore) {
        if (destinationStore == null) {
            throw new IllegalArgumentException("The destination store cannot be null.");
        }
        this.destinationStore = destinationStore;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void runInternal() {
        Event johnsonEvent = JohnsonUtils.raiseJohnsonEventRequiringTranslation(JohnsonEventType.ATTACHMENT_MIGRATION, "attachment.migration.message", null, JohnsonEventLevel.WARNING);
        try {
            AttachmentManager currentAttachmentManager = (AttachmentManager)ContainerManager.getComponent((String)"attachmentManager");
            AttachmentManager destinationAttachmentManager = this.getAttachmentManagerWithConfig(this.destinationStore);
            this.migrator = currentAttachmentManager.getMigrator(destinationAttachmentManager);
            this.migrator.setProgressMeter(this.progress);
            TransactionTemplate tt = new TransactionTemplate();
            tt.setTransactionManager((PlatformTransactionManager)ContainerManager.getInstance().getContainerContext().getComponent((Object)"transactionManager"));
            tt.execute((TransactionCallback)new TransactionCallbackWithoutResult(){

                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    AttachmentMigrationLongRunningTask.this.migrator.migrate();
                    SettingsManager settingsManager = (SettingsManager)ContainerManager.getComponent((String)"settingsManager");
                    Settings settings = settingsManager.getGlobalSettings();
                    settings.setAttachmentDataStore(AttachmentMigrationLongRunningTask.this.destinationStore);
                    settingsManager.updateGlobalSettings(settings);
                    AttachmentMigrationLongRunningTask.this.progress.setStatus("Migration completed successfully.");
                    AttachmentMigrationLongRunningTask.this.progress.setCompletedSuccessfully(true);
                    AttachmentMigrationLongRunningTask.this.progress.setPercentage(100);
                }
            });
        }
        catch (Exception e) {
            this.progress.setStatus("There was an error in the transfer. Please check your log files.");
            this.progress.setCompletedSuccessfully(false);
            log.error(e.getMessage(), (Throwable)e);
        }
        finally {
            JohnsonUtils.removeEvent(johnsonEvent);
        }
    }

    private AttachmentManager getAttachmentManagerWithConfig(String type) {
        if (type.equals("database.based.attachments.storage")) {
            return (AttachmentManager)ContainerManager.getInstance().getContainerContext().getComponent((Object)"databaseAttachmentManager");
        }
        if (type.equals("webdav.based.attachments.storage")) {
            log.warn("WebDAV attachment storage is no longer supported. Using default attachment manager.");
        }
        return (AttachmentManager)ContainerManager.getInstance().getContainerContext().getComponent((Object)"defaultAttachmentManager");
    }

    public String getName() {
        return "Attachment data migration";
    }
}

