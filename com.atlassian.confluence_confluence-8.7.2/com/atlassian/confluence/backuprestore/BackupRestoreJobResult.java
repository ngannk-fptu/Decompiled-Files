/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.backuprestore;

import java.io.Serializable;
import java.util.Objects;

public class BackupRestoreJobResult
implements Serializable {
    private long totalObjectsCount = 0L;
    private long processedObjectsCount = 0L;
    private long persistedObjectsCount = 0L;
    private long skippedObjectsCount = 0L;
    private long reusedObjectsCount = 0L;
    private long processedContentBodySizeInBytes = 0L;

    public long getTotalObjectsCount() {
        return this.totalObjectsCount;
    }

    public void setTotalObjectsCount(long totalObjectsCount) {
        this.totalObjectsCount = totalObjectsCount;
    }

    public long getProcessedObjectsCount() {
        return this.processedObjectsCount;
    }

    public void setProcessedObjectsCount(long processedObjectsCount) {
        this.processedObjectsCount = processedObjectsCount;
    }

    public long getPersistedObjectsCount() {
        return this.persistedObjectsCount;
    }

    public void setPersistedObjectsCount(long persistedObjectsCount) {
        this.persistedObjectsCount = persistedObjectsCount;
    }

    public long getSkippedObjectsCount() {
        return this.skippedObjectsCount;
    }

    public void setSkippedObjectsCount(long skippedObjectsCount) {
        this.skippedObjectsCount = skippedObjectsCount;
    }

    public long getReusedObjectsCount() {
        return this.reusedObjectsCount;
    }

    public void setReusedObjectsCount(long reusedObjectsCount) {
        this.reusedObjectsCount = reusedObjectsCount;
    }

    public long getProcessedContentBodySizeInBytes() {
        return this.processedContentBodySizeInBytes;
    }

    public void setProcessedContentBodySizeInBytes(long processedContentBodySizeInBytes) {
        this.processedContentBodySizeInBytes = processedContentBodySizeInBytes;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        BackupRestoreJobResult that = (BackupRestoreJobResult)o;
        return this.totalObjectsCount == that.totalObjectsCount && this.processedObjectsCount == that.processedObjectsCount && this.persistedObjectsCount == that.persistedObjectsCount && this.skippedObjectsCount == that.skippedObjectsCount && this.reusedObjectsCount == that.reusedObjectsCount && this.processedContentBodySizeInBytes == that.processedContentBodySizeInBytes;
    }

    public int hashCode() {
        return Objects.hash(this.totalObjectsCount, this.processedObjectsCount, this.persistedObjectsCount, this.skippedObjectsCount, this.reusedObjectsCount, this.processedContentBodySizeInBytes);
    }

    public String toString() {
        return "BackupRestoreJobResult{totalObjectsCount=" + this.totalObjectsCount + ", processedObjectsCount=" + this.processedObjectsCount + ", persistedObjectsCount=" + this.persistedObjectsCount + ", skippedObjectsCount=" + this.skippedObjectsCount + ", reusedObjectsCount=" + this.reusedObjectsCount + ", processedContentBodySizeInBytes=" + this.processedContentBodySizeInBytes + "}";
    }
}

