/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.audit.AuditLogAuthor
 *  com.atlassian.crowd.audit.AuditLogAuthorType
 *  com.atlassian.crowd.audit.AuditLogChangeset
 *  com.atlassian.crowd.audit.AuditLogEventSource
 *  com.atlassian.crowd.audit.AuditLogEventType
 *  com.atlassian.crowd.audit.ImmutableAuditLogAuthor
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Strings
 */
package com.atlassian.crowd.model.audit;

import com.atlassian.crowd.audit.AuditLogAuthor;
import com.atlassian.crowd.audit.AuditLogAuthorType;
import com.atlassian.crowd.audit.AuditLogChangeset;
import com.atlassian.crowd.audit.AuditLogEventSource;
import com.atlassian.crowd.audit.AuditLogEventType;
import com.atlassian.crowd.audit.ImmutableAuditLogAuthor;
import com.atlassian.crowd.model.audit.AuditLogEntityEntity;
import com.atlassian.crowd.model.audit.AuditLogEntryEntity;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class AuditLogChangesetEntity
implements AuditLogChangeset {
    private Long id;
    private Long timestamp;
    private AuditLogAuthorType authorType;
    private Long authorId;
    private String authorName = "";
    private AuditLogEventType eventType;
    private String ipAddress = "";
    private String eventMessage = "";
    private AuditLogEventSource source;
    private Set<AuditLogEntryEntity> entries = new HashSet<AuditLogEntryEntity>();
    private Set<AuditLogEntityEntity> entities = new HashSet<AuditLogEntityEntity>();

    public AuditLogChangesetEntity() {
    }

    public AuditLogChangesetEntity(AuditLogChangeset changeset) {
        this.id = changeset.getId();
        this.timestamp = changeset.getTimestampInstant().toEpochMilli();
        this.authorType = changeset.getAuthor().getType();
        this.authorId = changeset.getAuthor().getId();
        this.authorName = Strings.nullToEmpty((String)changeset.getAuthor().getName());
        this.eventType = changeset.getEventType();
        this.ipAddress = Strings.nullToEmpty((String)changeset.getIpAddress());
        this.eventMessage = Strings.nullToEmpty((String)changeset.getEventMessage());
        this.source = changeset.getSource();
        changeset.getEntities().stream().map(AuditLogEntityEntity::new).forEach(entity -> {
            this.entities.add((AuditLogEntityEntity)entity);
            entity.setChangeset(this);
        });
        changeset.getEntries().stream().map(AuditLogEntryEntity::new).forEach(auditLogEntry -> {
            this.entries.add((AuditLogEntryEntity)auditLogEntry);
            auditLogEntry.setChangeset(this);
        });
    }

    public Long getId() {
        return this.id;
    }

    public Instant getTimestampInstant() {
        return this.timestamp != null ? Instant.ofEpochMilli(this.timestamp) : null;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public AuditLogAuthorType getAuthorType() {
        return this.authorType;
    }

    public void setAuthorType(AuditLogAuthorType authorType) {
        this.authorType = authorType;
    }

    public Long getAuthorId() {
        return this.authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return this.authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = Strings.nullToEmpty((String)authorName);
    }

    public AuditLogEventType getEventType() {
        return this.eventType;
    }

    public void setEventType(AuditLogEventType eventType) {
        this.eventType = eventType;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = Strings.nullToEmpty((String)ipAddress);
    }

    public String getEventMessage() {
        return this.eventMessage;
    }

    public void setEventMessage(String eventMessage) {
        this.eventMessage = Strings.nullToEmpty((String)eventMessage);
    }

    public AuditLogEventSource getSource() {
        return this.source;
    }

    public void setSource(AuditLogEventSource source) {
        this.source = source;
    }

    public Set<AuditLogEntryEntity> getEntries() {
        return this.entries;
    }

    public void setEntries(Set<AuditLogEntryEntity> auditLogEntries) {
        this.entries = auditLogEntries;
    }

    public Set<AuditLogEntityEntity> getEntities() {
        return this.entities;
    }

    public void setEntities(Set<AuditLogEntityEntity> entities) {
        this.entities = entities;
    }

    public AuditLogAuthor getAuthor() {
        return new ImmutableAuditLogAuthor(this.authorId, this.authorName, this.authorType);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AuditLogChangesetEntity that = (AuditLogChangesetEntity)o;
        return Objects.equals(this.getId(), that.getId()) && Objects.equals(this.getTimestamp(), that.getTimestamp()) && Objects.equals(this.getAuthorType(), that.getAuthorType()) && Objects.equals(this.getAuthorId(), that.getAuthorId()) && Objects.equals(this.getAuthorName(), that.getAuthorName()) && Objects.equals(this.getEventType(), that.getEventType()) && Objects.equals(this.getIpAddress(), that.getIpAddress()) && Objects.equals(this.getEventMessage(), that.getEventMessage()) && Objects.equals(this.getSource(), that.getSource()) && Objects.equals(this.getEntries(), that.getEntries()) && Objects.equals(this.getEntities(), that.getEntities());
    }

    public int hashCode() {
        return Objects.hash(this.getId(), this.getTimestamp(), this.getAuthorType(), this.getAuthorId(), this.getAuthorName(), this.getEventType(), this.getIpAddress(), this.getEventMessage(), this.getSource(), this.getEntries(), this.getEntities());
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("id", (Object)this.id).add("timestamp", (Object)this.timestamp).add("authorType", (Object)this.authorType).add("authorId", (Object)this.authorId).add("authorName", (Object)this.authorName).add("eventType", (Object)this.eventType).add("ipAddress", (Object)this.ipAddress).add("eventMessage", (Object)this.eventMessage).add("entries", this.entries).add("entities", this.entities).add("source", (Object)this.source).toString();
    }
}

