/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.google.common.base.MoreObjects
 */
package com.atlassian.crowd.audit.query;

import com.atlassian.annotations.Internal;
import com.atlassian.crowd.audit.query.AuditLogQueryEntityRestriction;
import com.google.common.base.MoreObjects;
import java.util.Objects;

@Internal
public class AbstractAuditLogQueryRestriction {
    protected final Long id;
    protected final String name;
    protected final String namePrefix;

    public AbstractAuditLogQueryRestriction(String name, Long id, String namePrefix) {
        this.name = name;
        this.id = id;
        this.namePrefix = namePrefix;
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getNamePrefix() {
        return this.namePrefix;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AuditLogQueryEntityRestriction that = (AuditLogQueryEntityRestriction)o;
        return Objects.equals(this.id, that.id) && Objects.equals(this.name, that.name) && Objects.equals(this.namePrefix, that.namePrefix);
    }

    public int hashCode() {
        return Objects.hash(this.id, this.name, this.namePrefix);
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("id", (Object)this.id).add("name", (Object)this.name).add("namePrefix", (Object)this.namePrefix).toString();
    }
}

