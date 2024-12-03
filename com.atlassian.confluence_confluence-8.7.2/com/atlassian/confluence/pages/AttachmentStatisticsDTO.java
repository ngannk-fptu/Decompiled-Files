/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages;

public class AttachmentStatisticsDTO {
    private final int allAttachmentsCount;
    private final int currentAttachmentsCount;
    private final long allAttachmentsFileSize;
    private final long currentAttachmentsFileSize;
    private final long deletedAttachmentsFileSize;

    public AttachmentStatisticsDTO(int allAttachmentsCount, int currentAttachmentsCount, long allAttachmentsFileSize, long currentAttachmentsFileSize, long deletedAttachmentsFileSize) {
        this.allAttachmentsCount = allAttachmentsCount;
        this.currentAttachmentsCount = currentAttachmentsCount;
        this.allAttachmentsFileSize = allAttachmentsFileSize;
        this.currentAttachmentsFileSize = currentAttachmentsFileSize;
        this.deletedAttachmentsFileSize = deletedAttachmentsFileSize;
    }

    public int getAllAttachmentsCount() {
        return this.allAttachmentsCount;
    }

    public int getCurrentAttachmentsCount() {
        return this.currentAttachmentsCount;
    }

    public long getAllAttachmentsFileSize() {
        return this.allAttachmentsFileSize;
    }

    public long getCurrentAttachmentsFileSize() {
        return this.currentAttachmentsFileSize;
    }

    public long getDeletedAttachmentsFileSize() {
        return this.deletedAttachmentsFileSize;
    }
}

