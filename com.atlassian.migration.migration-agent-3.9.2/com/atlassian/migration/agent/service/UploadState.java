/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service;

import lombok.Generated;

public class UploadState {
    public final long numOfAttachments;
    public final long numOfAttachmentsAlreadyMigrated;
    public final long numOfUploadedAttachments;
    public final long uploadedBytes;
    public final int percentOfProgress;
    public final long totalBytes;
    public final long numOfFailedAttachments;
    public final long totalBytesToUpload;

    public UploadState(long numOfUploadedAttachments, long numOfAttachments, long numOfFailedAttachments, long uploadedBytes, long totalBytes, long totalBytesToUpload, long numOfAttachmentsAlreadyMigrated) {
        this.numOfUploadedAttachments = numOfUploadedAttachments;
        this.numOfAttachmentsAlreadyMigrated = numOfAttachmentsAlreadyMigrated;
        this.numOfFailedAttachments = numOfFailedAttachments;
        this.numOfAttachments = numOfAttachments;
        this.uploadedBytes = uploadedBytes;
        this.totalBytes = totalBytes;
        this.totalBytesToUpload = totalBytesToUpload;
        this.percentOfProgress = totalBytesToUpload == 0L ? 100 : (int)Math.min(this.uploadedBytes * 100L / totalBytesToUpload, 100L);
    }

    @Generated
    public long getNumOfAttachments() {
        return this.numOfAttachments;
    }

    @Generated
    public long getNumOfAttachmentsAlreadyMigrated() {
        return this.numOfAttachmentsAlreadyMigrated;
    }

    @Generated
    public long getNumOfUploadedAttachments() {
        return this.numOfUploadedAttachments;
    }

    @Generated
    public long getUploadedBytes() {
        return this.uploadedBytes;
    }

    @Generated
    public int getPercentOfProgress() {
        return this.percentOfProgress;
    }

    @Generated
    public long getTotalBytes() {
        return this.totalBytes;
    }

    @Generated
    public long getNumOfFailedAttachments() {
        return this.numOfFailedAttachments;
    }

    @Generated
    public long getTotalBytesToUpload() {
        return this.totalBytesToUpload;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof UploadState)) {
            return false;
        }
        UploadState other = (UploadState)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getNumOfAttachments() != other.getNumOfAttachments()) {
            return false;
        }
        if (this.getNumOfAttachmentsAlreadyMigrated() != other.getNumOfAttachmentsAlreadyMigrated()) {
            return false;
        }
        if (this.getNumOfUploadedAttachments() != other.getNumOfUploadedAttachments()) {
            return false;
        }
        if (this.getUploadedBytes() != other.getUploadedBytes()) {
            return false;
        }
        if (this.getPercentOfProgress() != other.getPercentOfProgress()) {
            return false;
        }
        if (this.getTotalBytes() != other.getTotalBytes()) {
            return false;
        }
        if (this.getNumOfFailedAttachments() != other.getNumOfFailedAttachments()) {
            return false;
        }
        return this.getTotalBytesToUpload() == other.getTotalBytesToUpload();
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof UploadState;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        long $numOfAttachments = this.getNumOfAttachments();
        result = result * 59 + (int)($numOfAttachments >>> 32 ^ $numOfAttachments);
        long $numOfAttachmentsAlreadyMigrated = this.getNumOfAttachmentsAlreadyMigrated();
        result = result * 59 + (int)($numOfAttachmentsAlreadyMigrated >>> 32 ^ $numOfAttachmentsAlreadyMigrated);
        long $numOfUploadedAttachments = this.getNumOfUploadedAttachments();
        result = result * 59 + (int)($numOfUploadedAttachments >>> 32 ^ $numOfUploadedAttachments);
        long $uploadedBytes = this.getUploadedBytes();
        result = result * 59 + (int)($uploadedBytes >>> 32 ^ $uploadedBytes);
        result = result * 59 + this.getPercentOfProgress();
        long $totalBytes = this.getTotalBytes();
        result = result * 59 + (int)($totalBytes >>> 32 ^ $totalBytes);
        long $numOfFailedAttachments = this.getNumOfFailedAttachments();
        result = result * 59 + (int)($numOfFailedAttachments >>> 32 ^ $numOfFailedAttachments);
        long $totalBytesToUpload = this.getTotalBytesToUpload();
        result = result * 59 + (int)($totalBytesToUpload >>> 32 ^ $totalBytesToUpload);
        return result;
    }

    @Generated
    public String toString() {
        return "UploadState(numOfAttachments=" + this.getNumOfAttachments() + ", numOfAttachmentsAlreadyMigrated=" + this.getNumOfAttachmentsAlreadyMigrated() + ", numOfUploadedAttachments=" + this.getNumOfUploadedAttachments() + ", uploadedBytes=" + this.getUploadedBytes() + ", percentOfProgress=" + this.getPercentOfProgress() + ", totalBytes=" + this.getTotalBytes() + ", numOfFailedAttachments=" + this.getNumOfFailedAttachments() + ", totalBytesToUpload=" + this.getTotalBytesToUpload() + ")";
    }
}

