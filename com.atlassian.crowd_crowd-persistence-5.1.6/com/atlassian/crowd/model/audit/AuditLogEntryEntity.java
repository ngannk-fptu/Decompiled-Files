/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.audit.AuditLogEntry
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Strings
 */
package com.atlassian.crowd.model.audit;

import com.atlassian.crowd.audit.AuditLogEntry;
import com.atlassian.crowd.model.audit.AuditLogChangesetEntity;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import java.util.Objects;

public class AuditLogEntryEntity
implements AuditLogEntry {
    private Long id;
    private String propertyName = "";
    private String oldValue = "";
    private String newValue = "";
    private AuditLogChangesetEntity changeset;

    protected AuditLogEntryEntity() {
    }

    public AuditLogEntryEntity(String propertyName, String oldValue, String newValue) {
        this(null, propertyName, oldValue, newValue);
    }

    public AuditLogEntryEntity(Long id, String propertyName, String oldValue, String newValue) {
        this.id = id;
        this.propertyName = Strings.nullToEmpty((String)propertyName);
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public AuditLogEntryEntity(Long id, String propertyName, String oldValue, String newValue, AuditLogChangesetEntity changeset) {
        this.id = id;
        this.propertyName = propertyName;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.changeset = changeset;
    }

    public AuditLogEntryEntity(AuditLogEntry auditLogEntry) {
        this(auditLogEntry.getPropertyName(), auditLogEntry.getOldValue(), auditLogEntry.getNewValue());
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = Strings.nullToEmpty((String)propertyName);
    }

    public String getOldValue() {
        return this.oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return this.newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public AuditLogChangesetEntity getChangeset() {
        return this.changeset;
    }

    public void setChangeset(AuditLogChangesetEntity changeset) {
        this.changeset = changeset;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AuditLogEntryEntity that = (AuditLogEntryEntity)o;
        return Objects.equals(this.getId(), that.getId()) && Objects.equals(this.getPropertyName(), that.getPropertyName()) && Objects.equals(this.getOldValue(), that.getOldValue()) && Objects.equals(this.getNewValue(), that.getNewValue());
    }

    public int hashCode() {
        return Objects.hash(this.getId(), this.getPropertyName(), this.getOldValue(), this.getNewValue());
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("id", (Object)this.id).add("propertyName", (Object)this.propertyName).add("oldValue", (Object)this.oldValue).add("newValue", (Object)this.newValue).toString();
    }
}

