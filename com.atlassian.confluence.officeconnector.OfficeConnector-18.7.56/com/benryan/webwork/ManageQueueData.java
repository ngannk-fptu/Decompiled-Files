/*
 * Decompiled with CFR 0.152.
 */
package com.benryan.webwork;

import com.benryan.components.DefaultSlideCacheManager;
import java.util.Date;

public class ManageQueueData {
    private final String ceoTitle;
    private final String fileName;
    private final long attachmentId;
    private final Date queueDate;

    public ManageQueueData(DefaultSlideCacheManager.QueueData taskData) {
        this.ceoTitle = taskData.getConversionData().getCeoName();
        this.fileName = taskData.getConversionData().getAttachmentName();
        this.attachmentId = taskData.getConversionData().getKey();
        this.queueDate = taskData.getConversionData().getQueueDate();
    }

    public String getCeoTitle() {
        return this.ceoTitle;
    }

    public String getFileName() {
        return this.fileName;
    }

    public Date getQueueDate() {
        return this.queueDate;
    }

    public long getAttachmentId() {
        return this.attachmentId;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (int)(this.attachmentId ^ this.attachmentId >>> 32);
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        ManageQueueData other = (ManageQueueData)obj;
        return this.attachmentId == other.attachmentId;
    }
}

