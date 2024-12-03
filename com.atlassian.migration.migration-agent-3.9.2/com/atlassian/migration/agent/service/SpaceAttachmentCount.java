/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service;

import lombok.Generated;

public class SpaceAttachmentCount {
    public final String cloudId;
    public final String spaceKey;
    public final long contentAttachmentCount;
    public final long retrievedMigAttachmentCount;
    public final long unRetrievableMigAttachmentCount;

    public boolean hasAllAttachmentsMigrated() {
        long totalMigAttachmentCount = this.retrievedMigAttachmentCount + this.unRetrievableMigAttachmentCount;
        return this.contentAttachmentCount == totalMigAttachmentCount;
    }

    public String getMessage() {
        return this.contentAttachmentCount + " total attachments for space " + this.spaceKey + " with " + this.retrievedMigAttachmentCount + " attachments migrated, " + this.unRetrievableMigAttachmentCount + " attachments not retrieved.";
    }

    @Generated
    public SpaceAttachmentCount(String cloudId, String spaceKey, long contentAttachmentCount, long retrievedMigAttachmentCount, long unRetrievableMigAttachmentCount) {
        this.cloudId = cloudId;
        this.spaceKey = spaceKey;
        this.contentAttachmentCount = contentAttachmentCount;
        this.retrievedMigAttachmentCount = retrievedMigAttachmentCount;
        this.unRetrievableMigAttachmentCount = unRetrievableMigAttachmentCount;
    }

    @Generated
    public String getCloudId() {
        return this.cloudId;
    }

    @Generated
    public String getSpaceKey() {
        return this.spaceKey;
    }

    @Generated
    public long getContentAttachmentCount() {
        return this.contentAttachmentCount;
    }

    @Generated
    public long getRetrievedMigAttachmentCount() {
        return this.retrievedMigAttachmentCount;
    }

    @Generated
    public long getUnRetrievableMigAttachmentCount() {
        return this.unRetrievableMigAttachmentCount;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SpaceAttachmentCount)) {
            return false;
        }
        SpaceAttachmentCount other = (SpaceAttachmentCount)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$cloudId = this.getCloudId();
        String other$cloudId = other.getCloudId();
        if (this$cloudId == null ? other$cloudId != null : !this$cloudId.equals(other$cloudId)) {
            return false;
        }
        String this$spaceKey = this.getSpaceKey();
        String other$spaceKey = other.getSpaceKey();
        if (this$spaceKey == null ? other$spaceKey != null : !this$spaceKey.equals(other$spaceKey)) {
            return false;
        }
        if (this.getContentAttachmentCount() != other.getContentAttachmentCount()) {
            return false;
        }
        if (this.getRetrievedMigAttachmentCount() != other.getRetrievedMigAttachmentCount()) {
            return false;
        }
        return this.getUnRetrievableMigAttachmentCount() == other.getUnRetrievableMigAttachmentCount();
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof SpaceAttachmentCount;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $cloudId = this.getCloudId();
        result = result * 59 + ($cloudId == null ? 43 : $cloudId.hashCode());
        String $spaceKey = this.getSpaceKey();
        result = result * 59 + ($spaceKey == null ? 43 : $spaceKey.hashCode());
        long $contentAttachmentCount = this.getContentAttachmentCount();
        result = result * 59 + (int)($contentAttachmentCount >>> 32 ^ $contentAttachmentCount);
        long $retrievedMigAttachmentCount = this.getRetrievedMigAttachmentCount();
        result = result * 59 + (int)($retrievedMigAttachmentCount >>> 32 ^ $retrievedMigAttachmentCount);
        long $unRetrievableMigAttachmentCount = this.getUnRetrievableMigAttachmentCount();
        result = result * 59 + (int)($unRetrievableMigAttachmentCount >>> 32 ^ $unRetrievableMigAttachmentCount);
        return result;
    }

    @Generated
    public String toString() {
        return "SpaceAttachmentCount(cloudId=" + this.getCloudId() + ", spaceKey=" + this.getSpaceKey() + ", contentAttachmentCount=" + this.getContentAttachmentCount() + ", retrievedMigAttachmentCount=" + this.getRetrievedMigAttachmentCount() + ", unRetrievableMigAttachmentCount=" + this.getUnRetrievableMigAttachmentCount() + ")";
    }
}

