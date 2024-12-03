/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.manager.audit.AuditLogConfiguration
 *  com.google.common.base.MoreObjects
 */
package com.atlassian.crowd.event.configuration;

import com.atlassian.crowd.manager.audit.AuditLogConfiguration;
import com.google.common.base.MoreObjects;
import java.util.Objects;

public class AuditLogConfigurationUpdatedEvent {
    private final AuditLogConfiguration oldConfiguration;
    private final AuditLogConfiguration newConfiguration;

    public AuditLogConfigurationUpdatedEvent(AuditLogConfiguration oldConfiguration, AuditLogConfiguration newConfiguration) {
        this.oldConfiguration = oldConfiguration;
        this.newConfiguration = newConfiguration;
    }

    public AuditLogConfiguration getOldConfiguration() {
        return this.oldConfiguration;
    }

    public AuditLogConfiguration getNewConfiguration() {
        return this.newConfiguration;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AuditLogConfigurationUpdatedEvent that = (AuditLogConfigurationUpdatedEvent)o;
        return Objects.equals(this.oldConfiguration, that.oldConfiguration) && Objects.equals(this.newConfiguration, that.newConfiguration);
    }

    public int hashCode() {
        return Objects.hash(this.oldConfiguration, this.newConfiguration);
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("oldConfiguration", (Object)this.oldConfiguration).add("newConfiguration", (Object)this.newConfiguration).toString();
    }
}

