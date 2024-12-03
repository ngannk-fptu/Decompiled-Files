/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.mma.model;

import com.atlassian.migration.agent.mma.model.SpaceMetadata;
import java.sql.Timestamp;
import org.codehaus.jackson.annotate.JsonProperty;

public class SpaceMetadataDTO
extends SpaceMetadata {
    @JsonProperty
    private final String cloudId;

    public SpaceMetadataDTO(long spaceId, String spaceKey, String spaceName, String spaceType, Long sumOfPageBlogDraftCount, Long attachmentSize, Long attachmentCount, Long estimatedMigrationTime, Timestamp lastModified, String cloudId) {
        super(spaceId, spaceKey, spaceName, spaceType, sumOfPageBlogDraftCount, attachmentSize, attachmentCount, estimatedMigrationTime, lastModified);
        this.cloudId = cloudId;
    }

    public SpaceMetadataDTO(SpaceMetadata spaceMetadata, String cloudId) {
        super(spaceMetadata.getSpaceId(), spaceMetadata.getSpaceKey(), spaceMetadata.getSpaceName(), spaceMetadata.getSpaceType(), spaceMetadata.getSumOfPageBlogDraftCount(), spaceMetadata.getAttachmentSize(), spaceMetadata.getAttachmentCount(), spaceMetadata.getEstimatedMigrationTime(), spaceMetadata.getLastModified());
        this.cloudId = cloudId;
    }
}

