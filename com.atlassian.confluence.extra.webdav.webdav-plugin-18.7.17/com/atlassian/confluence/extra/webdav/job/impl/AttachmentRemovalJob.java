/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 */
package com.atlassian.confluence.extra.webdav.job.impl;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.extra.webdav.job.ContentJob;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

public class AttachmentRemovalJob
implements ContentJob {
    private long containingPageId;
    private String fileName;
    private long creationTime;
    private transient AttachmentManager attachmentManager;
    private transient PageManager pageManager;

    public AttachmentRemovalJob(@ComponentImport PageManager pageManager, @ComponentImport AttachmentManager attachmentManager, long containingPageId, String fileName) {
        this.setContainingPageId(containingPageId);
        this.setFileName(fileName);
        this.setAttachmentManager(attachmentManager);
        this.setPageManager(pageManager);
        this.setCreationTime(System.currentTimeMillis());
    }

    @Override
    public long getMinimumAgeForExecution() {
        return 15000L;
    }

    public long getContainingPageId() {
        return this.containingPageId;
    }

    public void setContainingPageId(long containingPageId) {
        this.containingPageId = containingPageId;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public long getCreationTime() {
        return this.creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public void setAttachmentManager(AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }

    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    @Override
    public void execute() throws Exception {
        Attachment attachment;
        Page page = this.pageManager.getPage(this.getContainingPageId());
        if (null != page && null != (attachment = this.attachmentManager.getAttachment((ContentEntityObject)page, this.getFileName()))) {
            this.attachmentManager.removeAttachmentFromServer(attachment);
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AttachmentRemovalJob that = (AttachmentRemovalJob)o;
        if (this.containingPageId != that.containingPageId) {
            return false;
        }
        return !(this.fileName != null ? !this.fileName.equals(that.fileName) : that.fileName != null);
    }

    public int hashCode() {
        int result = (int)(this.containingPageId ^ this.containingPageId >>> 32);
        result = 31 * result + (this.fileName != null ? this.fileName.hashCode() : 0);
        return result;
    }

    public String toString() {
        return new StringBuffer(this.getClass().toString()).append(" [ Page ID: ").append(this.containingPageId).append("; Attachment File Name: ").append(this.fileName).append(" ]").toString();
    }
}

