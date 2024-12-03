/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.impl.backuprestore.backup.models;

import com.atlassian.confluence.impl.backuprestore.backup.exporters.converters.AbstractDatabaseDataConverter;
import com.atlassian.confluence.impl.backuprestore.backup.models.EntityObjectReadyForExport;
import com.google.common.annotations.VisibleForTesting;
import javax.annotation.Nonnull;

public class AttachmentInfo {
    private Long containerId;
    private Integer version;
    private Long id;
    private Long spaceId;
    private Long originalVersion;

    @VisibleForTesting
    public AttachmentInfo(@Nonnull Long id, @Nonnull Long containerId, @Nonnull Long spaceId, @Nonnull Integer version, Long originalVersion) {
        this.id = id;
        this.containerId = containerId;
        this.spaceId = spaceId;
        this.version = version;
        this.originalVersion = originalVersion;
    }

    public AttachmentInfo(@Nonnull EntityObjectReadyForExport entity) {
        this.id = AbstractDatabaseDataConverter.convertToLong(entity.getId().getValue());
        for (EntityObjectReadyForExport.Property property : entity.getProperties()) {
            if (!property.getName().equals("version")) continue;
            this.version = AbstractDatabaseDataConverter.convertToInteger(property.getValue());
            break;
        }
        for (EntityObjectReadyForExport.Reference reference : entity.getReferences()) {
            if (reference.getPropertyName().equals("space")) {
                this.spaceId = AbstractDatabaseDataConverter.convertToLong(reference.getReferencedId().getValue());
            }
            if (reference.getPropertyName().equals("containerContent")) {
                this.containerId = AbstractDatabaseDataConverter.convertToLong(reference.getReferencedId().getValue());
            }
            if (!reference.getPropertyName().equals("originalVersion")) continue;
            this.originalVersion = AbstractDatabaseDataConverter.convertToLong(reference.getReferencedId().getValue());
        }
    }

    public Long getId() {
        return this.id;
    }

    public Long getSpaceId() {
        return this.spaceId;
    }

    public Long getContainerId() {
        return this.containerId;
    }

    public Integer getVersion() {
        return this.version;
    }

    public Long getOriginalVersion() {
        return this.originalVersion;
    }

    public String toString() {
        return "AttachmentInfo {id=" + this.id + "; spaceId=" + this.spaceId + "; containerId=" + this.containerId + "; version=" + this.version + "; originalVersion=" + this.originalVersion + "}";
    }
}

