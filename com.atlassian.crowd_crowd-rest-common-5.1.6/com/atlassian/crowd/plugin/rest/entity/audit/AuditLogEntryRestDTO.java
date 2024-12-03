/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.audit.AuditLogEntry
 *  com.atlassian.crowd.audit.ImmutableAuditLogEntry$Builder
 *  com.google.common.base.MoreObjects
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.crowd.plugin.rest.entity.audit;

import com.atlassian.crowd.audit.AuditLogEntry;
import com.atlassian.crowd.audit.ImmutableAuditLogEntry;
import com.google.common.base.MoreObjects;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class AuditLogEntryRestDTO {
    @JsonProperty(value="propertyName")
    private final String propertyName;
    @JsonProperty(value="oldValue")
    private final String oldValue;
    @JsonProperty(value="newValue")
    private final String newValue;

    public AuditLogEntry toEntry() {
        return new ImmutableAuditLogEntry.Builder().setPropertyName(this.propertyName).setOldValue(this.oldValue).setNewValue(this.newValue).build();
    }

    @JsonCreator
    public AuditLogEntryRestDTO(@JsonProperty(value="propertyName") String propertyName, @JsonProperty(value="oldValue") String oldValue, @JsonProperty(value="newValue") String newValue) {
        this.propertyName = propertyName;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public String getOldValue() {
        return this.oldValue;
    }

    public String getNewValue() {
        return this.newValue;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AuditLogEntryRestDTO that = (AuditLogEntryRestDTO)o;
        return Objects.equals(this.getPropertyName(), that.getPropertyName()) && Objects.equals(this.getOldValue(), that.getOldValue()) && Objects.equals(this.getNewValue(), that.getNewValue());
    }

    public int hashCode() {
        return Objects.hash(this.getPropertyName(), this.getOldValue(), this.getNewValue());
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("propertyName", (Object)this.getPropertyName()).add("oldValue", (Object)this.getOldValue()).add("newValue", (Object)this.getNewValue()).toString();
    }
}

