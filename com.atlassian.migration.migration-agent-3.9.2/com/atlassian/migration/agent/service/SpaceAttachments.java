/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service;

import com.atlassian.migration.agent.entity.Attachment;
import com.atlassian.migration.agent.store.jpa.impl.StatelessResults;
import lombok.Generated;

public class SpaceAttachments
implements AutoCloseable {
    private final StatelessResults<Attachment> attachments;
    private final long sizeInBytes;

    @Override
    public void close() {
        this.attachments.close();
    }

    @Generated
    public SpaceAttachments(StatelessResults<Attachment> attachments, long sizeInBytes) {
        this.attachments = attachments;
        this.sizeInBytes = sizeInBytes;
    }

    @Generated
    public StatelessResults<Attachment> getAttachments() {
        return this.attachments;
    }

    @Generated
    public long getSizeInBytes() {
        return this.sizeInBytes;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SpaceAttachments)) {
            return false;
        }
        SpaceAttachments other = (SpaceAttachments)o;
        if (!other.canEqual(this)) {
            return false;
        }
        StatelessResults<Attachment> this$attachments = this.getAttachments();
        StatelessResults<Attachment> other$attachments = other.getAttachments();
        if (this$attachments == null ? other$attachments != null : !this$attachments.equals(other$attachments)) {
            return false;
        }
        return this.getSizeInBytes() == other.getSizeInBytes();
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof SpaceAttachments;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        StatelessResults<Attachment> $attachments = this.getAttachments();
        result = result * 59 + ($attachments == null ? 43 : $attachments.hashCode());
        long $sizeInBytes = this.getSizeInBytes();
        result = result * 59 + (int)($sizeInBytes >>> 32 ^ $sizeInBytes);
        return result;
    }

    @Generated
    public String toString() {
        return "SpaceAttachments(attachments=" + this.getAttachments() + ", sizeInBytes=" + this.getSizeInBytes() + ")";
    }
}

