/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Attachment
 *  com.google.common.base.Objects
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.plugins.files.model;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.plugins.files.api.FileContent;
import com.google.common.base.Objects;
import javax.annotation.Nonnull;

public class AttachmentContentImpl
implements FileContent {
    @Nonnull
    private final Attachment attachment;

    public AttachmentContentImpl(@Nonnull Attachment attachment) {
        this.attachment = attachment;
    }

    @Override
    @Nonnull
    public String getFileName() {
        return this.attachment.getFileName();
    }

    @Override
    @Nonnull
    public String getContentType() {
        return this.attachment.getMediaType();
    }

    @Override
    public long getFileSize() {
        return this.attachment.getFileSize();
    }

    @Override
    @Nonnull
    public String getDownloadUrl() {
        return this.attachment.getDownloadPath();
    }

    public Attachment getAttachment() {
        return this.attachment;
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.attachment});
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof AttachmentContentImpl)) {
            return false;
        }
        AttachmentContentImpl that = (AttachmentContentImpl)obj;
        return Objects.equal((Object)this.getAttachment(), (Object)that.getAttachment());
    }
}

