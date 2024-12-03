/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  lombok.Generated
 */
package com.atlassian.migration.agent.entity;

import javax.annotation.Nullable;
import lombok.Generated;

public class AttachmentCheckMetadata {
    private final long id;
    private final int version;
    private final long containerId;
    @Nullable
    private final Long previousVersion;
    @Nullable
    private final String title;
    private final long spaceId;
    @Nullable
    private final String spaceKey;

    @Generated
    public long getId() {
        return this.id;
    }

    @Generated
    public int getVersion() {
        return this.version;
    }

    @Generated
    public long getContainerId() {
        return this.containerId;
    }

    @Nullable
    @Generated
    public Long getPreviousVersion() {
        return this.previousVersion;
    }

    @Nullable
    @Generated
    public String getTitle() {
        return this.title;
    }

    @Generated
    public long getSpaceId() {
        return this.spaceId;
    }

    @Nullable
    @Generated
    public String getSpaceKey() {
        return this.spaceKey;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AttachmentCheckMetadata)) {
            return false;
        }
        AttachmentCheckMetadata other = (AttachmentCheckMetadata)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getId() != other.getId()) {
            return false;
        }
        if (this.getVersion() != other.getVersion()) {
            return false;
        }
        if (this.getContainerId() != other.getContainerId()) {
            return false;
        }
        Long this$previousVersion = this.getPreviousVersion();
        Long other$previousVersion = other.getPreviousVersion();
        if (this$previousVersion == null ? other$previousVersion != null : !((Object)this$previousVersion).equals(other$previousVersion)) {
            return false;
        }
        String this$title = this.getTitle();
        String other$title = other.getTitle();
        if (this$title == null ? other$title != null : !this$title.equals(other$title)) {
            return false;
        }
        if (this.getSpaceId() != other.getSpaceId()) {
            return false;
        }
        String this$spaceKey = this.getSpaceKey();
        String other$spaceKey = other.getSpaceKey();
        return !(this$spaceKey == null ? other$spaceKey != null : !this$spaceKey.equals(other$spaceKey));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof AttachmentCheckMetadata;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        long $id = this.getId();
        result = result * 59 + (int)($id >>> 32 ^ $id);
        result = result * 59 + this.getVersion();
        long $containerId = this.getContainerId();
        result = result * 59 + (int)($containerId >>> 32 ^ $containerId);
        Long $previousVersion = this.getPreviousVersion();
        result = result * 59 + ($previousVersion == null ? 43 : ((Object)$previousVersion).hashCode());
        String $title = this.getTitle();
        result = result * 59 + ($title == null ? 43 : $title.hashCode());
        long $spaceId = this.getSpaceId();
        result = result * 59 + (int)($spaceId >>> 32 ^ $spaceId);
        String $spaceKey = this.getSpaceKey();
        result = result * 59 + ($spaceKey == null ? 43 : $spaceKey.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "AttachmentCheckMetadata(id=" + this.getId() + ", version=" + this.getVersion() + ", containerId=" + this.getContainerId() + ", previousVersion=" + this.getPreviousVersion() + ", title=" + this.getTitle() + ", spaceId=" + this.getSpaceId() + ", spaceKey=" + this.getSpaceKey() + ")";
    }

    @Generated
    public AttachmentCheckMetadata(long id, int version, long containerId, @Nullable Long previousVersion, @Nullable String title, long spaceId, @Nullable String spaceKey) {
        this.id = id;
        this.version = version;
        this.containerId = containerId;
        this.previousVersion = previousVersion;
        this.title = title;
        this.spaceId = spaceId;
        this.spaceKey = spaceKey;
    }
}

