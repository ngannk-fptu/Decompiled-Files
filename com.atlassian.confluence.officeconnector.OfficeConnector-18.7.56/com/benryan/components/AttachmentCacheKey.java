/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Attachment
 */
package com.benryan.components;

import com.atlassian.confluence.pages.Attachment;
import java.io.Serializable;

public class AttachmentCacheKey
implements Serializable {
    final long id;
    final int version;
    final String viewName;

    public AttachmentCacheKey(Attachment attachment, String viewName) {
        this.id = attachment.getId();
        this.version = attachment.getVersion();
        this.viewName = viewName;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AttachmentCacheKey that = (AttachmentCacheKey)o;
        if (this.id != that.id) {
            return false;
        }
        if (this.version != that.version) {
            return false;
        }
        return !(this.viewName != null ? !this.viewName.equals(that.viewName) : that.viewName != null);
    }

    public int hashCode() {
        int result = (int)(this.id ^ this.id >>> 32);
        result = 31 * result + this.version;
        result = 31 * result + (this.viewName != null ? this.viewName.hashCode() : 0);
        return result;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("attachment-").append(this.id).append("-v").append(this.version);
        if (this.viewName != null) {
            sb.append('-').append(this.viewName);
        }
        return sb.toString();
    }
}

