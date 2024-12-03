/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.service.stepexecutor.attachment;

import com.atlassian.migration.agent.Tracker;
import com.atlassian.migration.agent.entity.Attachment;
import com.atlassian.migration.agent.entity.CloudSite;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.logging.LoggingContextBuilder;
import com.atlassian.migration.agent.media.MediaFileUploader;
import com.atlassian.migration.agent.media.MediaFileUploaderFactory;
import com.atlassian.migration.agent.service.AttachmentService;
import com.atlassian.migration.agent.service.stepexecutor.attachment.AttachmentDataProvider;
import com.atlassian.migration.agent.service.stepexecutor.attachment.AttachmentMigrationUploadStatus;
import org.slf4j.Logger;

public class AttachmentMigrator {
    private static final Logger log = ContextLoggerFactory.getLogger(AttachmentMigrator.class);
    private static final long MAXIMUM_FILE_SIZE = 0x50000000000L;
    private final AttachmentService attachmentService;
    private final MediaFileUploaderFactory mediaFileUploaderFactory;
    private final AttachmentDataProvider attachmentDataProvider;

    public AttachmentMigrator(AttachmentService attachmentService, MediaFileUploaderFactory mediaFileUploaderFactory, AttachmentDataProvider attachmentDataProvider) {
        this.attachmentService = attachmentService;
        this.mediaFileUploaderFactory = mediaFileUploaderFactory;
        this.attachmentDataProvider = attachmentDataProvider;
    }

    public AttachmentMigrationUploadStatus migrate(Attachment attachment, CloudSite cloudSite, Tracker uploadTracker) {
        boolean uploaded = this.upload(attachment, cloudSite, uploadTracker);
        if (uploaded) {
            return AttachmentMigrationUploadStatus.UPLOAD_SUCCESS;
        }
        return AttachmentMigrationUploadStatus.UPLOAD_FAILED;
    }

    private boolean upload(Attachment attachment, CloudSite cloudSite, Tracker progressTracker) {
        return LoggingContextBuilder.logCtx().withAttachment(attachment).execute(() -> this.uploadImpl(attachment, cloudSite, progressTracker));
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private boolean uploadImpl(Attachment attachment, CloudSite cloudSite, Tracker uploadTracker) {
        log.info("Start upload of attachment");
        MediaFileUploader mediaFileUploader = this.mediaFileUploaderFactory.create(cloudSite.getContainerToken());
        long attachmentId = attachment.getId();
        try (AttachmentDataProvider.AttachmentData attachmentData = this.attachmentDataProvider.getAttachmentData(attachmentId);){
            if (attachmentData.fileSize > 0x50000000000L) {
                log.error("Single file attachment size should not be larger than 5TB, attachmentId: {}", (Object)attachmentId);
                this.attachmentService.logFailedAttachmentMigration(cloudSite, attachment);
                boolean bl2 = false;
                return bl2;
            }
            String mediaId = mediaFileUploader.upload(attachmentData.inputStream, attachmentData.fileName, uploadTracker, attachmentData.fileSize);
            log.debug("Attachment Id: {} uploaded with filename: {} with mediaId: {}", new Object[]{attachmentId, attachmentData.fileName, mediaId});
            this.attachmentService.logSuccessfulAttachmentMigration(cloudSite, attachment, mediaId);
            boolean bl = true;
            return bl;
        }
        catch (AttachmentDataProvider.AttachmentDataRetrievalException ex) {
            log.warn("Failed to read attachment {} data. The error is unrecoverable. The attachment will be marked as broken and will not be retried.", (Object)attachmentId, (Object)ex);
            this.attachmentService.logFailedAttachmentMigration(cloudSite, attachment);
            return false;
        }
    }
}

