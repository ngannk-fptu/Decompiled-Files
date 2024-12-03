/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.audit.AuditLogEntityType
 *  com.google.common.base.MoreObjects
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.audit;

import com.atlassian.crowd.audit.AuditLogEntity;
import com.atlassian.crowd.audit.AuditLogEntityType;
import com.google.common.base.MoreObjects;
import java.util.Objects;
import javax.annotation.Nullable;

public class ImmutableAuditLogEntity
implements AuditLogEntity {
    private final AuditLogEntityType entityType;
    private final Long entityId;
    private final String entityName;
    private final boolean primary;

    public ImmutableAuditLogEntity(Builder builder) {
        this.entityType = builder.entityType;
        this.entityId = builder.entityId;
        this.entityName = builder.entityName;
        this.primary = builder.primary;
    }

    @Override
    @Nullable
    public AuditLogEntityType getEntityType() {
        return this.entityType;
    }

    @Override
    @Nullable
    public Long getEntityId() {
        return this.entityId;
    }

    @Override
    @Nullable
    public String getEntityName() {
        return this.entityName;
    }

    @Override
    public boolean isPrimary() {
        return this.primary;
    }

    public static ImmutableAuditLogEntity from(AuditLogEntity original) {
        return original instanceof ImmutableAuditLogEntity ? (ImmutableAuditLogEntity)original : new Builder(original).build();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ImmutableAuditLogEntity that = (ImmutableAuditLogEntity)o;
        return this.entityType == that.entityType && Objects.equals(this.entityId, that.entityId) && Objects.equals(this.entityName, that.entityName) && Objects.equals(this.primary, that.primary);
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("entityType", (Object)this.entityType).add("entityId", (Object)this.entityId).add("entityName", (Object)this.entityName).add("primary", this.primary).toString();
    }

    public int hashCode() {
        return Objects.hash(this.entityType, this.entityId, this.entityName, this.primary);
    }

    public static class Builder {
        private AuditLogEntityType entityType;
        private Long entityId;
        private String entityName;
        private boolean primary;

        public Builder() {
        }

        public Builder(AuditLogEntity entity) {
            this.entityType = entity.getEntityType();
            this.entityId = entity.getEntityId();
            this.entityName = entity.getEntityName();
            this.primary = entity.isPrimary();
        }

        public Builder setEntityType(AuditLogEntityType entityType) {
            this.entityType = entityType;
            return this;
        }

        public Builder setEntityId(Long entityId) {
            this.entityId = entityId;
            return this;
        }

        public Builder setEntityName(String entityName) {
            this.entityName = entityName;
            return this;
        }

        public Builder setPrimary() {
            this.primary = true;
            return this;
        }

        public ImmutableAuditLogEntity build() {
            return new ImmutableAuditLogEntity(this);
        }
    }
}

