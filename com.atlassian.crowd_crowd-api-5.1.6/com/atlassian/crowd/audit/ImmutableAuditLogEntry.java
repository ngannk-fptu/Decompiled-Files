/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 */
package com.atlassian.crowd.audit;

import com.atlassian.crowd.audit.AuditLogEntry;
import com.google.common.base.MoreObjects;
import java.util.Objects;

public class ImmutableAuditLogEntry
implements AuditLogEntry {
    private final String propertyName;
    private final String oldValue;
    private final String newValue;

    public ImmutableAuditLogEntry(String propertyName, String oldValue, String newValue) {
        this.propertyName = propertyName;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    @Override
    public String getPropertyName() {
        return this.propertyName;
    }

    @Override
    public String getOldValue() {
        return this.oldValue;
    }

    @Override
    public String getNewValue() {
        return this.newValue;
    }

    public static ImmutableAuditLogEntry from(AuditLogEntry auditLogEntry) {
        return auditLogEntry instanceof ImmutableAuditLogEntry ? (ImmutableAuditLogEntry)auditLogEntry : new Builder(auditLogEntry).build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ImmutableAuditLogEntry that = (ImmutableAuditLogEntry)o;
        return Objects.equals(this.propertyName, that.propertyName) && Objects.equals(this.oldValue, that.oldValue) && Objects.equals(this.newValue, that.newValue);
    }

    public int hashCode() {
        return Objects.hash(this.propertyName, this.oldValue, this.newValue);
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("propertyName", (Object)this.propertyName).add("oldValue", (Object)this.oldValue).add("newValue", (Object)this.newValue).toString();
    }

    public static class Builder {
        private String propertyName;
        private String oldValue;
        private String newValue;

        public Builder() {
        }

        public Builder(AuditLogEntry entry) {
            this.propertyName = entry.getPropertyName();
            this.oldValue = entry.getOldValue();
            this.newValue = entry.getNewValue();
        }

        public Builder setPropertyName(String propertyName) {
            this.propertyName = propertyName;
            return this;
        }

        public Builder setOldValue(String oldValue) {
            this.oldValue = oldValue;
            return this;
        }

        public Builder setNewValue(String newValue) {
            this.newValue = newValue;
            return this;
        }

        public ImmutableAuditLogEntry build() {
            return new ImmutableAuditLogEntry(this.propertyName, this.oldValue, this.newValue);
        }
    }
}

