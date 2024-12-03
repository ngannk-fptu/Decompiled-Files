/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.entity;

import java.io.Serializable;
import java.util.Objects;

public class AttachmentMigrationId
implements Serializable {
    private String cloudId;
    private long attachmentId;

    private AttachmentMigrationId() {
    }

    public AttachmentMigrationId(String cloudId, long attachmentId) {
        this.cloudId = cloudId;
        this.attachmentId = attachmentId;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AttachmentMigrationId that = (AttachmentMigrationId)o;
        return Objects.equals(this.cloudId, that.cloudId) && Objects.equals(this.attachmentId, that.attachmentId);
    }

    public int hashCode() {
        return Objects.hash(this.cloudId, this.attachmentId);
    }
}

