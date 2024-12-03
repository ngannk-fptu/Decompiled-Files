/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.service.stepexecutor.attachment;

import com.atlassian.migration.agent.entity.Attachment;
import com.atlassian.migration.agent.entity.CloudSite;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.service.MigrationErrorCode;
import com.atlassian.migration.agent.service.impl.StepType;
import com.atlassian.migration.agent.service.stepexecutor.StepExecutionException;
import com.atlassian.migration.agent.service.stepexecutor.attachment.AttachmentMigrationUploadStatus;
import com.atlassian.migration.agent.service.stepexecutor.attachment.AttachmentMigrator;
import com.atlassian.migration.agent.service.stepexecutor.attachment.AttachmentsUploadTracker;
import com.atlassian.migration.agent.service.util.StopConditionCheckingUtil;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import org.slf4j.Logger;

class AttachmentsToMigrateConsumer
implements Runnable {
    private static final Logger log = ContextLoggerFactory.getLogger(AttachmentsToMigrateConsumer.class);
    private final BlockingQueue<Attachment> buffer;
    private final CloudSite cloudSite;
    private final AttachmentMigrator attachmentMigrator;
    private final AttachmentsUploadTracker progressTracker;
    private final BooleanSupplier allAttachmentsProduced;
    private final BooleanSupplier forceStop;
    private final String migrationId;

    AttachmentsToMigrateConsumer(CloudSite cloudSite, BlockingQueue<Attachment> buffer, AttachmentMigrator attachmentMigrator, AttachmentsUploadTracker progressTracker, BooleanSupplier allAttachmentsProduced, BooleanSupplier forceStop, String migrationId) {
        this.buffer = buffer;
        this.cloudSite = cloudSite;
        this.attachmentMigrator = attachmentMigrator;
        this.progressTracker = progressTracker;
        this.allAttachmentsProduced = allAttachmentsProduced;
        this.forceStop = forceStop;
        this.migrationId = migrationId;
    }

    @Override
    public void run() {
        block9: {
            while (true) {
                Attachment attachment;
                if (this.forceStop.getAsBoolean() || StopConditionCheckingUtil.isStopConditionReached()) {
                    log.info("Stop requested. Will exit...");
                    break block9;
                }
                try {
                    attachment = this.buffer.poll(200L, TimeUnit.MILLISECONDS);
                }
                catch (InterruptedException ex) {
                    throw new StepExecutionException(MigrationErrorCode.ATTACHMENT_MIGRATION_INTERRUPTED, StepType.ATTACHMENT_UPLOAD, this.migrationId, "Failed to poll attachment from buffer", ex);
                }
                if (attachment != null) {
                    AttachmentMigrationUploadStatus uploadStatus = this.attachmentMigrator.migrate(attachment, this.cloudSite, this.progressTracker::addUploadedBytes);
                    switch (uploadStatus) {
                        case UPLOAD_SUCCESS: {
                            this.progressTracker.attachmentUploaded();
                            break;
                        }
                        case UPLOAD_FAILED: {
                            this.progressTracker.attachmentUploadFailed();
                            break;
                        }
                    }
                    log.debug("Attachment {}, upload migration attachment status {}", (Object)attachment.getId(), (Object)uploadStatus.name());
                    continue;
                }
                if (this.allAttachmentsProduced.getAsBoolean()) break;
            }
            log.info("No more attachments to upload. Will exit...");
        }
    }
}

