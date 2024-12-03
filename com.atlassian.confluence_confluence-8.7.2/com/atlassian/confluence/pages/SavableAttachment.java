/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages;

import com.atlassian.confluence.pages.Attachment;
import java.io.InputStream;

public class SavableAttachment {
    private Attachment attachment;
    private Attachment previousVersion;
    private InputStream attachmentData;

    public SavableAttachment(Attachment attachment, Attachment previousVersion, InputStream attachmentData) {
        this.attachment = attachment;
        this.previousVersion = previousVersion;
        this.attachmentData = attachmentData;
    }

    public Attachment getAttachment() {
        return this.attachment;
    }

    public void setAttachment(Attachment attachment) {
        this.attachment = attachment;
    }

    public Attachment getPreviousVersion() {
        return this.previousVersion;
    }

    public void setPreviousVersion(Attachment previousVersion) {
        this.previousVersion = previousVersion;
    }

    public InputStream getAttachmentData() {
        return this.attachmentData;
    }

    public void setAttachmentData(InputStream attachmentData) {
        this.attachmentData = attachmentData;
    }
}

