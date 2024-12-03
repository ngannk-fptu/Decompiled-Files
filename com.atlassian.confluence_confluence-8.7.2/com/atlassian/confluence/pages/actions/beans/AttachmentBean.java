/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.xwork.ParameterSafe
 *  org.springframework.util.Assert
 */
package com.atlassian.confluence.pages.actions.beans;

import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.xwork.ParameterSafe;
import org.springframework.util.Assert;

@ParameterSafe
public class AttachmentBean {
    private String fileName;
    private int version = 0;
    private String mimeType;

    public AttachmentBean() {
        boolean i = false;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Attachment retrieveMatchingAttachment(AbstractPage page, AttachmentManager attachmentManager) {
        Assert.notNull((Object)this.fileName);
        return attachmentManager.getAttachment(page, this.fileName, this.version);
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return this.mimeType;
    }
}

