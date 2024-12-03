/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.audit.AuditLogEntity
 *  com.atlassian.crowd.audit.AuditLogEntityType
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Strings
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.model.audit;

import com.atlassian.crowd.audit.AuditLogEntity;
import com.atlassian.crowd.audit.AuditLogEntityType;
import com.atlassian.crowd.model.audit.AuditLogChangesetEntity;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import java.util.Objects;
import javax.annotation.Nullable;

public class AuditLogEntityEntity
implements AuditLogEntity {
    private Long id;
    private AuditLogEntityType entityType;
    private String entityName = "";
    private Long entityId;
    private boolean primary;
    private AuditLogChangesetEntity changeset;

    public AuditLogEntityEntity() {
    }

    public AuditLogEntityEntity(AuditLogEntity entity) {
        this(entity.getEntityType(), entity.getEntityName(), entity.getEntityId(), entity.isPrimary());
    }

    public AuditLogEntityEntity(AuditLogEntityType entityType, String entityName, Long entityId, boolean primary) {
        this(null, entityType, entityName, entityId, primary);
    }

    public AuditLogEntityEntity(Long id, AuditLogEntityType entityType, String entityName, Long entityId, boolean primary) {
        this(id, entityType, entityName, entityId, primary, null);
    }

    public AuditLogEntityEntity(Long id, AuditLogEntityType entityType, String entityName, Long entityId, boolean primary, AuditLogChangesetEntity changeset) {
        this.id = id;
        this.entityType = entityType;
        this.entityName = Strings.nullToEmpty((String)entityName);
        this.entityId = entityId;
        this.primary = primary;
        this.changeset = changeset;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Nullable
    public String getEntityName() {
        return this.entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = Strings.nullToEmpty((String)entityName);
    }

    @Nullable
    public AuditLogEntityType getEntityType() {
        return this.entityType;
    }

    public void setEntityType(AuditLogEntityType entityType) {
        this.entityType = entityType;
    }

    @Nullable
    public Long getEntityId() {
        return this.entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public boolean isPrimary() {
        return this.primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
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
        AuditLogEntityEntity that = (AuditLogEntityEntity)o;
        return this.primary == that.primary && Objects.equals(this.id, that.id) && this.entityType == that.entityType && Objects.equals(this.entityName, that.entityName) && Objects.equals(this.entityId, that.entityId);
    }

    public int hashCode() {
        return Objects.hash(this.id, this.entityType, this.entityName, this.entityId, this.primary);
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("id", (Object)this.id).add("entityType", (Object)this.entityType).add("entityName", (Object)this.entityName).add("entityId", (Object)this.entityId).add("primary", this.primary).toString();
    }
}

