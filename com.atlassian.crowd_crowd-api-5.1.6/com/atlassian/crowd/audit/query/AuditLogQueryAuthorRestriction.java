/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.crowd.audit.AuditLogAuthorType
 *  com.google.common.base.MoreObjects
 */
package com.atlassian.crowd.audit.query;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.crowd.audit.AuditLogAuthorType;
import com.atlassian.crowd.audit.query.AuditLogQueryEntityRestriction;
import com.google.common.base.MoreObjects;
import java.util.Objects;

@ExperimentalApi
public class AuditLogQueryAuthorRestriction
extends AuditLogQueryEntityRestriction {
    private final AuditLogAuthorType type;

    private AuditLogQueryAuthorRestriction(Long id, String name, String namePrefix, AuditLogAuthorType type) {
        super(id, name, namePrefix);
        this.type = type;
    }

    public static AuditLogQueryAuthorRestriction id(Long id, AuditLogAuthorType type) {
        return new AuditLogQueryAuthorRestriction(id, null, null, type);
    }

    public static AuditLogQueryAuthorRestriction name(String name, AuditLogAuthorType type) {
        return new AuditLogQueryAuthorRestriction(null, name, null, type);
    }

    public static AuditLogQueryAuthorRestriction namePrefix(String namePrefix, AuditLogAuthorType type) {
        return new AuditLogQueryAuthorRestriction(null, null, namePrefix, type);
    }

    public static AuditLogQueryAuthorRestriction type(AuditLogAuthorType type) {
        return new AuditLogQueryAuthorRestriction(null, null, null, type);
    }

    public AuditLogAuthorType getType() {
        return this.type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        AuditLogQueryAuthorRestriction that = (AuditLogQueryAuthorRestriction)o;
        return this.type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.type);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("type", (Object)this.type).toString();
    }
}

