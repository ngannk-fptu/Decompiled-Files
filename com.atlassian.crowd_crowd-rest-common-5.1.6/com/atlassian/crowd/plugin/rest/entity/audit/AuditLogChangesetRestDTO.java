/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.audit.AuditLogAuthorType
 *  com.atlassian.crowd.audit.AuditLogChangeset
 *  com.atlassian.crowd.audit.AuditLogEntityType
 *  com.atlassian.crowd.audit.AuditLogEventSource
 *  com.atlassian.crowd.audit.AuditLogEventType
 *  com.atlassian.crowd.audit.ImmutableAuditLogChangeset$Builder
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Sets
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.annotate.JsonDeserialize
 *  org.codehaus.jackson.map.annotate.JsonSerialize
 */
package com.atlassian.crowd.plugin.rest.entity.audit;

import com.atlassian.crowd.audit.AuditLogAuthorType;
import com.atlassian.crowd.audit.AuditLogChangeset;
import com.atlassian.crowd.audit.AuditLogEntityType;
import com.atlassian.crowd.audit.AuditLogEventSource;
import com.atlassian.crowd.audit.AuditLogEventType;
import com.atlassian.crowd.audit.ImmutableAuditLogChangeset;
import com.atlassian.crowd.plugin.rest.entity.audit.AuditLogAuthorRestDTO;
import com.atlassian.crowd.plugin.rest.entity.audit.AuditLogEntityRestDTO;
import com.atlassian.crowd.plugin.rest.entity.audit.AuditLogEntryRestDTO;
import com.atlassian.crowd.plugin.rest.util.ISO8601DateDeserializer;
import com.atlassian.crowd.plugin.rest.util.ISO8601DateSerializer;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

public class AuditLogChangesetRestDTO {
    @JsonProperty(value="id")
    private final Long id;
    @JsonProperty(value="timestamp")
    @JsonSerialize(using=ISO8601DateSerializer.class)
    @JsonDeserialize(using=ISO8601DateDeserializer.class)
    private final Date timestamp;
    @JsonProperty(value="author")
    private final AuditLogAuthorRestDTO author;
    @JsonProperty(value="authorType")
    @Deprecated
    private final AuditLogAuthorType authorType;
    @JsonProperty(value="authorId")
    @Deprecated
    private final Long authorId;
    @JsonProperty(value="authorName")
    @Deprecated
    private final String authorName;
    @JsonProperty(value="eventType")
    private final AuditLogEventType eventType;
    @JsonProperty(value="entityType")
    @Deprecated
    private final AuditLogEntityType entityType;
    @JsonProperty(value="entityId")
    @Deprecated
    private final Long entityId;
    @JsonProperty(value="entityName")
    @Deprecated
    private final String entityName;
    @JsonProperty(value="entities")
    private final Set<AuditLogEntityRestDTO> entities;
    @JsonProperty(value="ipAddress")
    private final String ipAddress;
    @JsonProperty(value="eventMessage")
    private final String eventMessage;
    @JsonProperty(value="source")
    private final AuditLogEventSource source;
    @JsonProperty(value="entries")
    private final Set<AuditLogEntryRestDTO> entries;

    public AuditLogChangeset toChangeset() {
        AuditLogAuthorRestDTO effectiveAuthor = this.author == null && this.authorType != null ? AuditLogAuthorRestDTO.builder().setType(this.authorType).setName(this.authorName).setId(this.authorId).build() : this.author;
        Set<Object> effectiveEntities = this.entities == null && this.entityType != null ? Collections.singleton(AuditLogEntityRestDTO.builder().setId(this.entityId).setName(this.entityName).setType(this.entityType).setPrimary(true).build()) : (this.entities == null ? Collections.emptySet() : this.entities);
        Preconditions.checkArgument((effectiveAuthor != null ? 1 : 0) != 0, (Object)"Author not set");
        Preconditions.checkArgument((this.eventType != null ? 1 : 0) != 0, (Object)"Event type not set");
        return new ImmutableAuditLogChangeset.Builder().setId(this.id).setTimestamp(this.timestamp == null ? null : this.timestamp.toInstant()).setAuthor(effectiveAuthor.toAuthor()).setEventType(this.eventType).setSource(this.source == null ? AuditLogEventSource.MANUAL : this.source).setEntities((Collection)effectiveEntities.stream().map(AuditLogEntityRestDTO::toEntity).collect(Collectors.toList())).setIpAddress(this.ipAddress).setEventMessage(this.eventMessage).setEntries(this.entries != null ? (Collection)this.entries.stream().map(AuditLogEntryRestDTO::toEntry).collect(Collectors.toList()) : Collections.emptyList()).build();
    }

    @JsonCreator
    public AuditLogChangesetRestDTO(@JsonProperty(value="id") Long id, @JsonProperty(value="timestamp") Date timestamp, @JsonProperty(value="author") AuditLogAuthorRestDTO author, @JsonProperty(value="authorType") AuditLogAuthorType authorType, @JsonProperty(value="authorId") Long authorId, @JsonProperty(value="authorName") String authorName, @JsonProperty(value="eventType") AuditLogEventType eventType, @JsonProperty(value="entityType") AuditLogEntityType entityType, @JsonProperty(value="entityId") Long entityId, @JsonProperty(value="entityName") String entityName, @JsonProperty(value="entities") Set<AuditLogEntityRestDTO> entities, @JsonProperty(value="ipAddress") String ipAddress, @JsonProperty(value="eventMessage") String eventMessage, @JsonProperty(value="source") AuditLogEventSource source, @JsonProperty(value="entries") Set<AuditLogEntryRestDTO> entries) {
        this.id = id;
        this.timestamp = timestamp;
        this.author = author;
        this.authorType = authorType;
        this.authorId = authorId;
        this.authorName = authorName;
        this.eventType = eventType;
        this.entityType = entityType;
        this.entityId = entityId;
        this.entityName = entityName;
        this.entities = entities != null ? ImmutableSet.copyOf(entities) : null;
        this.ipAddress = ipAddress;
        this.eventMessage = eventMessage;
        this.source = source;
        this.entries = entries != null ? ImmutableSet.copyOf(entries) : null;
    }

    public Long getId() {
        return this.id;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public AuditLogAuthorRestDTO getAuthor() {
        return this.author;
    }

    public AuditLogAuthorType getAuthorType() {
        return this.authorType;
    }

    public Long getAuthorId() {
        return this.authorId;
    }

    public String getAuthorName() {
        return this.authorName;
    }

    public AuditLogEventType getEventType() {
        return this.eventType;
    }

    public AuditLogEntityType getEntityType() {
        return this.entityType;
    }

    public Long getEntityId() {
        return this.entityId;
    }

    public String getEntityName() {
        return this.entityName;
    }

    public Set<AuditLogEntityRestDTO> getEntities() {
        return this.entities;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public String getEventMessage() {
        return this.eventMessage;
    }

    public AuditLogEventSource getSource() {
        return this.source;
    }

    public Set<AuditLogEntryRestDTO> getEntries() {
        return this.entries;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(AuditLogChangesetRestDTO data) {
        return new Builder(data);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AuditLogChangesetRestDTO that = (AuditLogChangesetRestDTO)o;
        return Objects.equals(this.getId(), that.getId()) && Objects.equals(this.getTimestamp(), that.getTimestamp()) && Objects.equals(this.getAuthor(), that.getAuthor()) && Objects.equals(this.getAuthorType(), that.getAuthorType()) && Objects.equals(this.getAuthorId(), that.getAuthorId()) && Objects.equals(this.getAuthorName(), that.getAuthorName()) && Objects.equals(this.getEventType(), that.getEventType()) && Objects.equals(this.getEntityType(), that.getEntityType()) && Objects.equals(this.getEntityId(), that.getEntityId()) && Objects.equals(this.getEntityName(), that.getEntityName()) && Objects.equals(this.getEntities(), that.getEntities()) && Objects.equals(this.getIpAddress(), that.getIpAddress()) && Objects.equals(this.getEventMessage(), that.getEventMessage()) && Objects.equals(this.getSource(), that.getSource()) && Objects.equals(this.getEntries(), that.getEntries());
    }

    public int hashCode() {
        return Objects.hash(this.getId(), this.getTimestamp(), this.getAuthor(), this.getAuthorType(), this.getAuthorId(), this.getAuthorName(), this.getEventType(), this.getEntityType(), this.getEntityId(), this.getEntityName(), this.getEntities(), this.getIpAddress(), this.getEventMessage(), this.getSource(), this.getEntries());
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("id", (Object)this.getId()).add("timestamp", (Object)this.getTimestamp()).add("author", (Object)this.getAuthor()).add("authorType", (Object)this.getAuthorType()).add("authorId", (Object)this.getAuthorId()).add("authorName", (Object)this.getAuthorName()).add("eventType", (Object)this.getEventType()).add("entityType", (Object)this.getEntityType()).add("entityId", (Object)this.getEntityId()).add("entityName", (Object)this.getEntityName()).add("entities", this.getEntities()).add("ipAddress", (Object)this.getIpAddress()).add("eventMessage", (Object)this.getEventMessage()).add("source", (Object)this.getSource()).add("entries", this.getEntries()).toString();
    }

    public static final class Builder {
        private Long id;
        private Date timestamp;
        private AuditLogAuthorRestDTO author;
        private AuditLogAuthorType authorType;
        private Long authorId;
        private String authorName;
        private AuditLogEventType eventType;
        private AuditLogEntityType entityType;
        private Long entityId;
        private String entityName;
        private Set<AuditLogEntityRestDTO> entities = Sets.newHashSet();
        private String ipAddress;
        private String eventMessage;
        private AuditLogEventSource source;
        private Set<AuditLogEntryRestDTO> entries = Sets.newHashSet();

        private Builder() {
        }

        private Builder(AuditLogChangesetRestDTO initialData) {
            this.id = initialData.getId();
            this.timestamp = initialData.getTimestamp();
            this.author = initialData.getAuthor();
            this.authorType = initialData.getAuthorType();
            this.authorId = initialData.getAuthorId();
            this.authorName = initialData.getAuthorName();
            this.eventType = initialData.getEventType();
            this.entityType = initialData.getEntityType();
            this.entityId = initialData.getEntityId();
            this.entityName = initialData.getEntityName();
            this.entities = Sets.newHashSet(initialData.getEntities());
            this.ipAddress = initialData.getIpAddress();
            this.eventMessage = initialData.getEventMessage();
            this.source = initialData.getSource();
            this.entries = Sets.newHashSet(initialData.getEntries());
        }

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setTimestamp(Date timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder setAuthor(AuditLogAuthorRestDTO author) {
            this.author = author;
            return this;
        }

        public Builder setAuthorType(AuditLogAuthorType authorType) {
            this.authorType = authorType;
            return this;
        }

        public Builder setAuthorId(Long authorId) {
            this.authorId = authorId;
            return this;
        }

        public Builder setAuthorName(String authorName) {
            this.authorName = authorName;
            return this;
        }

        public Builder setEventType(AuditLogEventType eventType) {
            this.eventType = eventType;
            return this;
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

        public Builder setEntities(Set<AuditLogEntityRestDTO> entities) {
            this.entities = entities;
            return this;
        }

        public Builder addEntity(AuditLogEntityRestDTO entity) {
            this.entities.add(entity);
            return this;
        }

        public Builder addEntities(Iterable<AuditLogEntityRestDTO> entities) {
            for (AuditLogEntityRestDTO entity : entities) {
                this.addEntity(entity);
            }
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

        public Builder setEntries(Set<AuditLogEntryRestDTO> entries) {
            this.entries = entries;
            return this;
        }

        public Builder addEntry(AuditLogEntryRestDTO entry) {
            this.entries.add(entry);
            return this;
        }

        public Builder addEntries(Iterable<AuditLogEntryRestDTO> entries) {
            for (AuditLogEntryRestDTO entry : entries) {
                this.addEntry(entry);
            }
            return this;
        }

        public AuditLogChangesetRestDTO build() {
            return new AuditLogChangesetRestDTO(this.id, this.timestamp, this.author, this.authorType, this.authorId, this.authorName, this.eventType, this.entityType, this.entityId, this.entityName, this.entities, this.ipAddress, this.eventMessage, this.source, this.entries);
        }
    }
}

