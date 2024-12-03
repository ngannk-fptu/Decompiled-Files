/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.stepexecutor.attachment;

import com.atlassian.migration.agent.service.UploadState;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

class AttachmentsUploadTracker {
    private final AtomicLong numOfUploadedAttachments = new AtomicLong();
    private final AtomicLong numOfFailedAttachments = new AtomicLong();
    private final AtomicLong uploadedBytes = new AtomicLong();
    private final long numOfAttachments;
    private final long totalSizeToUpload;
    private final long totalSize;
    private final long numOfAttachmentsAlreadyMigrated;
    private final Consumer<UploadState> onProgress;

    AttachmentsUploadTracker(long numOfAttachments, long totalSize, long numOfAttachmentsAlreadyMigrated, long totalSizeToUpload, Consumer<UploadState> onProgress) {
        this.numOfAttachments = numOfAttachments;
        this.totalSize = totalSize;
        this.numOfAttachmentsAlreadyMigrated = numOfAttachmentsAlreadyMigrated;
        this.onProgress = onProgress;
        this.totalSizeToUpload = totalSizeToUpload;
    }

    UploadState getUploadState() {
        return new UploadState(this.numOfUploadedAttachments.get(), this.numOfAttachments, this.numOfFailedAttachments.get(), this.uploadedBytes.get(), this.totalSize, this.totalSizeToUpload, this.numOfAttachmentsAlreadyMigrated);
    }

    void addUploadedBytes(long uploadedBytes) {
        this.uploadedBytes.addAndGet(uploadedBytes);
        this.onProgress.accept(this.getUploadState());
    }

    void attachmentUploaded() {
        this.numOfUploadedAttachments.incrementAndGet();
        this.onProgress.accept(this.getUploadState());
    }

    void attachmentUploadFailed() {
        this.numOfFailedAttachments.incrementAndGet();
        this.onProgress.accept(this.getUploadState());
    }
}

