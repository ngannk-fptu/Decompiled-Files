/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.synchrony;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.NotExportable;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Snapshots
implements NotExportable {
    private String key;
    private byte[] value;
    private long contentId;
    private Date inserted;
    private ContentEntityObject sharedDraft;

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    private byte[] getValue() {
        return this.value;
    }

    private void setValue(byte[] value) {
        this.value = value;
    }

    public long getContentId() {
        return this.contentId;
    }

    public void setContentId(long contentId) {
        this.contentId = contentId;
    }

    public Date getInserted() {
        return this.inserted;
    }

    public void setInserted(Date inserted) {
        this.inserted = inserted;
    }

    public ContentEntityObject getSharedDraft() {
        return this.sharedDraft;
    }

    public void setSharedDraft(ContentEntityObject sharedDraft) {
        this.sharedDraft = sharedDraft;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Snapshots snapshots = (Snapshots)o;
        return Objects.equals(this.key, snapshots.key) && Arrays.equals(this.value, snapshots.value);
    }

    public int hashCode() {
        return Objects.hash(this.key, Arrays.hashCode(this.value));
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("key", (Object)this.key).append("contentId", this.contentId).append("inserted", (Object)this.inserted).toString();
    }
}

