/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.mma.model;

import java.sql.Timestamp;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonProperty;

public class SpaceMetadata {
    @JsonProperty
    private final long spaceId;
    @JsonProperty
    private final String spaceKey;
    @JsonProperty
    private final String spaceName;
    @JsonProperty
    private final String spaceType;
    @JsonProperty
    private final Long sumOfPageBlogDraftCount;
    @JsonProperty
    private final Long attachmentSize;
    @JsonProperty
    private final Long attachmentCount;
    @JsonProperty
    private final Long estimatedMigrationTime;
    @JsonProperty
    private final Timestamp lastModified;

    @Generated
    public long getSpaceId() {
        return this.spaceId;
    }

    @Generated
    public String getSpaceKey() {
        return this.spaceKey;
    }

    @Generated
    public String getSpaceName() {
        return this.spaceName;
    }

    @Generated
    public String getSpaceType() {
        return this.spaceType;
    }

    @Generated
    public Long getSumOfPageBlogDraftCount() {
        return this.sumOfPageBlogDraftCount;
    }

    @Generated
    public Long getAttachmentSize() {
        return this.attachmentSize;
    }

    @Generated
    public Long getAttachmentCount() {
        return this.attachmentCount;
    }

    @Generated
    public Long getEstimatedMigrationTime() {
        return this.estimatedMigrationTime;
    }

    @Generated
    public Timestamp getLastModified() {
        return this.lastModified;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SpaceMetadata)) {
            return false;
        }
        SpaceMetadata other = (SpaceMetadata)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getSpaceId() != other.getSpaceId()) {
            return false;
        }
        String this$spaceKey = this.getSpaceKey();
        String other$spaceKey = other.getSpaceKey();
        if (this$spaceKey == null ? other$spaceKey != null : !this$spaceKey.equals(other$spaceKey)) {
            return false;
        }
        String this$spaceName = this.getSpaceName();
        String other$spaceName = other.getSpaceName();
        if (this$spaceName == null ? other$spaceName != null : !this$spaceName.equals(other$spaceName)) {
            return false;
        }
        String this$spaceType = this.getSpaceType();
        String other$spaceType = other.getSpaceType();
        if (this$spaceType == null ? other$spaceType != null : !this$spaceType.equals(other$spaceType)) {
            return false;
        }
        Long this$sumOfPageBlogDraftCount = this.getSumOfPageBlogDraftCount();
        Long other$sumOfPageBlogDraftCount = other.getSumOfPageBlogDraftCount();
        if (this$sumOfPageBlogDraftCount == null ? other$sumOfPageBlogDraftCount != null : !((Object)this$sumOfPageBlogDraftCount).equals(other$sumOfPageBlogDraftCount)) {
            return false;
        }
        Long this$attachmentSize = this.getAttachmentSize();
        Long other$attachmentSize = other.getAttachmentSize();
        if (this$attachmentSize == null ? other$attachmentSize != null : !((Object)this$attachmentSize).equals(other$attachmentSize)) {
            return false;
        }
        Long this$attachmentCount = this.getAttachmentCount();
        Long other$attachmentCount = other.getAttachmentCount();
        if (this$attachmentCount == null ? other$attachmentCount != null : !((Object)this$attachmentCount).equals(other$attachmentCount)) {
            return false;
        }
        Long this$estimatedMigrationTime = this.getEstimatedMigrationTime();
        Long other$estimatedMigrationTime = other.getEstimatedMigrationTime();
        if (this$estimatedMigrationTime == null ? other$estimatedMigrationTime != null : !((Object)this$estimatedMigrationTime).equals(other$estimatedMigrationTime)) {
            return false;
        }
        Timestamp this$lastModified = this.getLastModified();
        Timestamp other$lastModified = other.getLastModified();
        return !(this$lastModified == null ? other$lastModified != null : !((Object)this$lastModified).equals(other$lastModified));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof SpaceMetadata;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        long $spaceId = this.getSpaceId();
        result = result * 59 + (int)($spaceId >>> 32 ^ $spaceId);
        String $spaceKey = this.getSpaceKey();
        result = result * 59 + ($spaceKey == null ? 43 : $spaceKey.hashCode());
        String $spaceName = this.getSpaceName();
        result = result * 59 + ($spaceName == null ? 43 : $spaceName.hashCode());
        String $spaceType = this.getSpaceType();
        result = result * 59 + ($spaceType == null ? 43 : $spaceType.hashCode());
        Long $sumOfPageBlogDraftCount = this.getSumOfPageBlogDraftCount();
        result = result * 59 + ($sumOfPageBlogDraftCount == null ? 43 : ((Object)$sumOfPageBlogDraftCount).hashCode());
        Long $attachmentSize = this.getAttachmentSize();
        result = result * 59 + ($attachmentSize == null ? 43 : ((Object)$attachmentSize).hashCode());
        Long $attachmentCount = this.getAttachmentCount();
        result = result * 59 + ($attachmentCount == null ? 43 : ((Object)$attachmentCount).hashCode());
        Long $estimatedMigrationTime = this.getEstimatedMigrationTime();
        result = result * 59 + ($estimatedMigrationTime == null ? 43 : ((Object)$estimatedMigrationTime).hashCode());
        Timestamp $lastModified = this.getLastModified();
        result = result * 59 + ($lastModified == null ? 43 : ((Object)$lastModified).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "SpaceMetadata(spaceId=" + this.getSpaceId() + ", spaceKey=" + this.getSpaceKey() + ", spaceName=" + this.getSpaceName() + ", spaceType=" + this.getSpaceType() + ", sumOfPageBlogDraftCount=" + this.getSumOfPageBlogDraftCount() + ", attachmentSize=" + this.getAttachmentSize() + ", attachmentCount=" + this.getAttachmentCount() + ", estimatedMigrationTime=" + this.getEstimatedMigrationTime() + ", lastModified=" + this.getLastModified() + ")";
    }

    @Generated
    public SpaceMetadata(long spaceId, String spaceKey, String spaceName, String spaceType, Long sumOfPageBlogDraftCount, Long attachmentSize, Long attachmentCount, Long estimatedMigrationTime, Timestamp lastModified) {
        this.spaceId = spaceId;
        this.spaceKey = spaceKey;
        this.spaceName = spaceName;
        this.spaceType = spaceType;
        this.sumOfPageBlogDraftCount = sumOfPageBlogDraftCount;
        this.attachmentSize = attachmentSize;
        this.attachmentCount = attachmentCount;
        this.estimatedMigrationTime = estimatedMigrationTime;
        this.lastModified = lastModified;
    }
}

