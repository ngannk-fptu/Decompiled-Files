/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.bean.EntityObject
 */
package com.atlassian.confluence.pages;

import com.atlassian.confluence.core.FileExportable;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.core.bean.EntityObject;
import java.io.InputStream;

public class AttachmentData
extends EntityObject
implements FileExportable {
    private int version;
    private Attachment attachment;
    private InputStream data;
    private int hibernateVersion;

    public AttachmentData() {
    }

    public AttachmentData(int version, Attachment attachment, InputStream data) {
        this.setVersion(version);
        this.setAttachment(attachment);
        this.setData(data);
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Attachment getAttachment() {
        return this.attachment;
    }

    public void setAttachment(Attachment attachment) {
        this.attachment = attachment;
    }

    public InputStream getData() {
        return this.data;
    }

    public void setData(InputStream data) {
        this.data = data;
    }

    protected int getHibernateVersion() {
        return this.hibernateVersion;
    }

    protected void setHibernateVersion(int hibernateVersion) {
        this.hibernateVersion = hibernateVersion;
    }
}

