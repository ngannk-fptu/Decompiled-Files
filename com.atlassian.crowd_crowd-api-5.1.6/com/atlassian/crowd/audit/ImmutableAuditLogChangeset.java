/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.audit.AuditLogAuthorType
 *  com.atlassian.crowd.audit.AuditLogEntityType
 *  com.atlassian.crowd.audit.AuditLogEventSource
 *  com.atlassian.crowd.audit.AuditLogEventType
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nonnull
 */
package com.atlassian.crowd.audit;

import com.atlassian.crowd.audit.AuditLogAuthor;
import com.atlassian.crowd.audit.AuditLogAuthorType;
import com.atlassian.crowd.audit.AuditLogChangeset;
import com.atlassian.crowd.audit.AuditLogEntity;
import com.atlassian.crowd.audit.AuditLogEntityType;
import com.atlassian.crowd.audit.AuditLogEntry;
import com.atlassian.crowd.audit.AuditLogEventSource;
import com.atlassian.crowd.audit.AuditLogEventType;
import com.atlassian.crowd.audit.ImmutableAuditLogAuthor;
import com.atlassian.crowd.audit.ImmutableAuditLogEntity;
import com.atlassian.crowd.audit.ImmutableAuditLogEntry;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public class ImmutableAuditLogChangeset
implements AuditLogChangeset {
    private final Long id;
    private final Instant timestamp;
    private final AuditLogAuthor author;
    private final AuditLogEventType eventType;
    private final String ipAddress;
    private final String eventMessage;
    private final AuditLogEventSource source;
    private final Set<ImmutableAuditLogEntry> entries;
    private final Set<ImmutableAuditLogEntity> entities;

    @Deprecated
    public ImmutableAuditLogChangeset(Long id, Instant timestamp, AuditLogAuthorType authorType, Long authorId, String authorName, AuditLogEventType eventType, AuditLogEntityType entityType, Long entityId, String entityName, String ipAddress, String eventMessage, List<ImmutableAuditLogEntry> entries) {
        this.id = id;
        this.timestamp = timestamp;
        this.author = new ImmutableAuditLogAuthor(authorId, authorName, authorType);
        this.eventType = eventType;
        this.ipAddress = ipAddress;
        this.eventMessage = eventMessage;
        this.source = AuditLogEventSource.MANUAL;
        this.entries = ImmutableSet.copyOf(entries);
        this.entities = ImmutableAuditLogChangeset.hasEntity(entityType, entityId, entityName) ? Collections.singleton(new ImmutableAuditLogEntity.Builder().setEntityType(entityType).setEntityId(entityId).setEntityName(entityName).setPrimary().build()) : Collections.emptySet();
    }

    private ImmutableAuditLogChangeset(Builder builder) {
        this.id = builder.id;
        this.timestamp = builder.timestamp;
        this.author = builder.author;
        this.eventType = builder.eventType;
        this.ipAddress = builder.ipAddress;
        this.eventMessage = builder.eventMessage;
        this.entries = builder.entries;
        this.entities = builder.entities;
        this.source = builder.source;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public Instant getTimestampInstant() {
        return this.timestamp;
    }

    @Override
    public AuditLogAuthorType getAuthorType() {
        return this.author.getType();
    }

    @Override
    public Long getAuthorId() {
        return this.author.getId();
    }

    @Override
    public String getAuthorName() {
        return this.author.getName();
    }

    @Override
    public AuditLogAuthor getAuthor() {
        return this.author;
    }

    @Override
    public AuditLogEventType getEventType() {
        return this.eventType;
    }

    @Override
    public String getIpAddress() {
        return this.ipAddress;
    }

    @Override
    public String getEventMessage() {
        return this.eventMessage;
    }

    @Override
    public AuditLogEventSource getSource() {
        return this.source;
    }

    public Collection<ImmutableAuditLogEntry> getEntries() {
        return this.entries;
    }

    public Collection<ImmutableAuditLogEntity> getEntities() {
        return this.entities;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ImmutableAuditLogChangeset that = (ImmutableAuditLogChangeset)o;
        return Objects.equals(this.id, that.id) && Objects.equals(this.timestamp, that.timestamp) && Objects.equals(this.author, that.author) && this.eventType == that.eventType && Objects.equals(this.ipAddress, that.ipAddress) && Objects.equals(this.eventMessage, that.eventMessage) && Objects.equals(this.source, that.source) && Objects.equals(this.entries, that.entries) && Objects.equals(this.entities, that.entities);
    }

    public int hashCode() {
        return Objects.hash(this.id, this.timestamp, this.author, this.eventType, this.ipAddress, this.eventMessage, this.source, this.entries, this.entities);
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("id", (Object)this.id).add("timestamp", (Object)this.timestamp).add("author", (Object)this.author).add("eventType", (Object)this.eventType).add("ipAddress", (Object)this.ipAddress).add("eventMessage", (Object)this.eventMessage).add("entries", this.entries).add("entities", this.entities).toString();
    }

    public static ImmutableAuditLogChangeset from(AuditLogChangeset changeset) {
        if (changeset instanceof ImmutableAuditLogChangeset) {
            return (ImmutableAuditLogChangeset)changeset;
        }
        return new Builder(changeset).build();
    }

    private static boolean hasEntity(AuditLogEntityType entityType, Long entityId, String entityName) {
        return entityId != null || entityType != null || entityName != null;
    }

    public static class Builder {
        private Long id;
        private Instant timestamp;
        private AuditLogAuthor author = new ImmutableAuditLogAuthor(null, null, null);
        private AuditLogEventType eventType;
        private AuditLogEntityType entityType;
        private Long entityId;
        private String entityName;
        private String ipAddress;
        private String eventMessage;
        private AuditLogEventSource source = AuditLogEventSource.MANUAL;
        private Set<ImmutableAuditLogEntry> entries = new HashSet<ImmutableAuditLogEntry>();
        private Set<ImmutableAuditLogEntity> entities = new HashSet<ImmutableAuditLogEntity>();

        public Builder() {
        }

        public Builder(AuditLogChangeset changeset) {
            this.id = changeset.getId();
            this.timestamp = changeset.getTimestampInstant();
            this.author = changeset.getAuthor();
            this.eventType = changeset.getEventType();
            this.ipAddress = changeset.getIpAddress();
            this.eventMessage = changeset.getEventMessage();
            this.source = changeset.getSource();
            this.entries = changeset.getEntries().stream().map(ImmutableAuditLogEntry.Builder::new).map(ImmutableAuditLogEntry.Builder::build).collect(Collectors.toSet());
            this.entities = changeset.getEntities().stream().map(ImmutableAuditLogEntity::from).collect(Collectors.toSet());
        }

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setTimestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder setTimestamp(@Nonnull Date timestamp) {
            this.timestamp = timestamp.toInstant();
            return this;
        }

        public Builder setTimestamp(@Nonnull Long timestamp) {
            this.timestamp = Instant.ofEpochMilli(timestamp);
            return this;
        }

        @Deprecated
        public Builder setAuthorType(AuditLogAuthorType authorType) {
            this.author = new ImmutableAuditLogAuthor(this.author.getId(), this.author.getName(), authorType);
            return this;
        }

        @Deprecated
        public Builder setAuthorId(Long authorId) {
            this.author = new ImmutableAuditLogAuthor(authorId, this.author.getName(), this.author.getType());
            return this;
        }

        @Deprecated
        public Builder setAuthorName(String authorName) {
            this.author = new ImmutableAuditLogAuthor(this.author.getId(), authorName, this.author.getType());
            return this;
        }

        public Builder setAuthor(AuditLogAuthor author) {
            this.author = new ImmutableAuditLogAuthor(author);
            return this;
        }

        public Builder setEventType(AuditLogEventType eventType) {
            this.eventType = eventType;
            return this;
        }

        @Deprecated
        public Builder setEntityType(AuditLogEntityType entityType) {
            this.entityType = entityType;
            return this;
        }

        @Deprecated
        public Builder setEntityId(Long entityId) {
            this.entityId = entityId;
            return this;
        }

        @Deprecated
        public Builder setEntityName(String entityName) {
            this.entityName = entityName;
            return this;
        }

        public Builder setIpAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        public Builder setEventMessage(String eventMessage) {
            this.eventMessage = eventMessage;
            return this;
        }

        public Builder setSource(AuditLogEventSource source) {
            this.source = source;
            return this;
        }

        @Deprecated
        public Builder setEntries(List<ImmutableAuditLogEntry> entries) {
            this.entries = new HashSet<ImmutableAuditLogEntry>(entries);
            return this;
        }

        public Builder setEntries(Collection<? extends AuditLogEntry> entries) {
            this.entries = entries.stream().map(entry -> new ImmutableAuditLogEntry.Builder((AuditLogEntry)entry).build()).collect(Collectors.toSet());
            return this;
        }

        public Builder setEntities(Collection<? extends AuditLogEntity> entities) {
            this.entities = entities.stream().map(ImmutableAuditLogEntity::from).collect(Collectors.toSet());
            return this;
        }

        public Builder addEntry(AuditLogEntry entry) {
            this.entries.add(ImmutableAuditLogEntry.from(entry));
            return this;
        }

        public Builder addEntry(ImmutableAuditLogEntry entry) {
            this.entries.add(entry);
            return this;
        }

        public Builder addEntries(Collection<AuditLogEntry> entry) {
            this.entries.addAll(entry.stream().map(e -> new ImmutableAuditLogEntry.Builder((AuditLogEntry)e).build()).collect(Collectors.toList()));
            return this;
        }

        public Builder addEntity(AuditLogEntity entity) {
            this.entities.add(ImmutableAuditLogEntity.from(entity));
            return this;
        }

        public Builder addEntity(ImmutableAuditLogEntity entity) {
            this.entities.add(entity);
            return this;
        }

        public ImmutableAuditLogChangeset build() {
            if (ImmutableAuditLogChangeset.hasEntity(this.entityType, this.entityId, this.entityName)) {
                this.entities.add(new ImmutableAuditLogEntity.Builder().setEntityType(this.entityType).setEntityId(this.entityId).setEntityName(this.entityName).setPrimary().build());
            }
            return new ImmutableAuditLogChangeset(this);
        }
    }
}

