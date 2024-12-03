/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.audit.AuditLogAuthorType
 *  com.atlassian.crowd.audit.AuditLogEntityType
 *  com.atlassian.crowd.audit.AuditLogEventSource
 *  com.atlassian.crowd.audit.AuditLogEventType
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.audit;

import com.atlassian.crowd.audit.AuditLogAuthor;
import com.atlassian.crowd.audit.AuditLogAuthorType;
import com.atlassian.crowd.audit.AuditLogEntity;
import com.atlassian.crowd.audit.AuditLogEntityType;
import com.atlassian.crowd.audit.AuditLogEntry;
import com.atlassian.crowd.audit.AuditLogEventSource;
import com.atlassian.crowd.audit.AuditLogEventType;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public interface AuditLogChangeset {
    public Long getId();

    public Instant getTimestampInstant();

    public AuditLogAuthor getAuthor();

    @Deprecated
    public AuditLogAuthorType getAuthorType();

    @Deprecated
    @Nullable
    public Long getAuthorId();

    @Deprecated
    @Nullable
    public String getAuthorName();

    public AuditLogEventType getEventType();

    default public Optional<AuditLogEntity> getEntity() {
        if (this.getEntities().isEmpty()) {
            return Optional.empty();
        }
        Optional<AuditLogEntity> primaryObject = this.getEntities().stream().filter(AuditLogEntity::isPrimary).findFirst();
        return primaryObject.map(Optional::of).orElseGet(() -> Optional.of(this.getEntities().iterator().next()));
    }

    default public List<AuditLogEntity> getAdditionalEntities() {
        return this.getAdditionalEntities().stream().filter(ao -> !ao.isPrimary()).collect(Collectors.toList());
    }

    @Nullable
    @Deprecated
    default public AuditLogEntityType getEntityType() {
        Optional<AuditLogEntity> primaryObject = this.getEntity();
        if (primaryObject.isPresent()) {
            return primaryObject.get().getEntityType();
        }
        return null;
    }

    @Nullable
    @Deprecated
    default public Long getEntityId() {
        Optional<AuditLogEntity> primaryObject = this.getEntity();
        if (primaryObject.isPresent()) {
            return primaryObject.get().getEntityId();
        }
        return null;
    }

    @Nullable
    @Deprecated
    default public String getEntityName() {
        Optional<AuditLogEntity> primaryObject = this.getEntity();
        if (primaryObject.isPresent()) {
            return primaryObject.get().getEntityName();
        }
        return null;
    }

    @Nullable
    public String getIpAddress();

    @Nullable
    public String getEventMessage();

    public AuditLogEventSource getSource();

    public Collection<? extends AuditLogEntry> getEntries();

    public Collection<? extends AuditLogEntity> getEntities();
}

