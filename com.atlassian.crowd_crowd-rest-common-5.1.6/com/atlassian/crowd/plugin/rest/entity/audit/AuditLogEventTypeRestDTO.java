/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.audit.AuditLogEventType
 *  com.google.common.base.MoreObjects
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.crowd.plugin.rest.entity.audit;

import com.atlassian.crowd.audit.AuditLogEventType;
import com.google.common.base.MoreObjects;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class AuditLogEventTypeRestDTO {
    @JsonProperty
    private final AuditLogEventType type;
    @JsonProperty
    private final String displayName;

    @JsonCreator
    public AuditLogEventTypeRestDTO(@JsonProperty(value="type") AuditLogEventType type, @JsonProperty(value="displayName") String displayName) {
        this.type = type;
        this.displayName = displayName;
    }

    public AuditLogEventType getType() {
        return this.type;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AuditLogEventTypeRestDTO that = (AuditLogEventTypeRestDTO)o;
        return Objects.equals(this.getType(), that.getType()) && Objects.equals(this.getDisplayName(), that.getDisplayName());
    }

    public int hashCode() {
        return Objects.hash(this.getType(), this.getDisplayName());
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("type", (Object)this.getType()).add("displayName", (Object)this.getDisplayName()).toString();
    }
}

