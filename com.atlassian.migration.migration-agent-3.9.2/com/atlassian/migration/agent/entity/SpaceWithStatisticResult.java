/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.entity;

import java.sql.Timestamp;
import lombok.Generated;

public class SpaceWithStatisticResult {
    private final long id;
    private final String key;
    private final String name;
    private final String spaceType;
    private final String status;
    private final Long pageBlogDraftCount;
    private final Long attachmentSize;
    private final Long attachmentCount;
    private final Long teamCalendarCount;
    private final Long estimatedMigrationTime;
    private final Timestamp lastModified;

    @Generated
    public long getId() {
        return this.id;
    }

    @Generated
    public String getKey() {
        return this.key;
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public String getSpaceType() {
        return this.spaceType;
    }

    @Generated
    public String getStatus() {
        return this.status;
    }

    @Generated
    public Long getPageBlogDraftCount() {
        return this.pageBlogDraftCount;
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
    public Long getTeamCalendarCount() {
        return this.teamCalendarCount;
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
        if (!(o instanceof SpaceWithStatisticResult)) {
            return false;
        }
        SpaceWithStatisticResult other = (SpaceWithStatisticResult)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getId() != other.getId()) {
            return false;
        }
        String this$key = this.getKey();
        String other$key = other.getKey();
        if (this$key == null ? other$key != null : !this$key.equals(other$key)) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
            return false;
        }
        String this$spaceType = this.getSpaceType();
        String other$spaceType = other.getSpaceType();
        if (this$spaceType == null ? other$spaceType != null : !this$spaceType.equals(other$spaceType)) {
            return false;
        }
        String this$status = this.getStatus();
        String other$status = other.getStatus();
        if (this$status == null ? other$status != null : !this$status.equals(other$status)) {
            return false;
        }
        Long this$pageBlogDraftCount = this.getPageBlogDraftCount();
        Long other$pageBlogDraftCount = other.getPageBlogDraftCount();
        if (this$pageBlogDraftCount == null ? other$pageBlogDraftCount != null : !((Object)this$pageBlogDraftCount).equals(other$pageBlogDraftCount)) {
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
        Long this$teamCalendarCount = this.getTeamCalendarCount();
        Long other$teamCalendarCount = other.getTeamCalendarCount();
        if (this$teamCalendarCount == null ? other$teamCalendarCount != null : !((Object)this$teamCalendarCount).equals(other$teamCalendarCount)) {
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
        return other instanceof SpaceWithStatisticResult;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        long $id = this.getId();
        result = result * 59 + (int)($id >>> 32 ^ $id);
        String $key = this.getKey();
        result = result * 59 + ($key == null ? 43 : $key.hashCode());
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        String $spaceType = this.getSpaceType();
        result = result * 59 + ($spaceType == null ? 43 : $spaceType.hashCode());
        String $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : $status.hashCode());
        Long $pageBlogDraftCount = this.getPageBlogDraftCount();
        result = result * 59 + ($pageBlogDraftCount == null ? 43 : ((Object)$pageBlogDraftCount).hashCode());
        Long $attachmentSize = this.getAttachmentSize();
        result = result * 59 + ($attachmentSize == null ? 43 : ((Object)$attachmentSize).hashCode());
        Long $attachmentCount = this.getAttachmentCount();
        result = result * 59 + ($attachmentCount == null ? 43 : ((Object)$attachmentCount).hashCode());
        Long $teamCalendarCount = this.getTeamCalendarCount();
        result = result * 59 + ($teamCalendarCount == null ? 43 : ((Object)$teamCalendarCount).hashCode());
        Long $estimatedMigrationTime = this.getEstimatedMigrationTime();
        result = result * 59 + ($estimatedMigrationTime == null ? 43 : ((Object)$estimatedMigrationTime).hashCode());
        Timestamp $lastModified = this.getLastModified();
        result = result * 59 + ($lastModified == null ? 43 : ((Object)$lastModified).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "SpaceWithStatisticResult(id=" + this.getId() + ", key=" + this.getKey() + ", name=" + this.getName() + ", spaceType=" + this.getSpaceType() + ", status=" + this.getStatus() + ", pageBlogDraftCount=" + this.getPageBlogDraftCount() + ", attachmentSize=" + this.getAttachmentSize() + ", attachmentCount=" + this.getAttachmentCount() + ", teamCalendarCount=" + this.getTeamCalendarCount() + ", estimatedMigrationTime=" + this.getEstimatedMigrationTime() + ", lastModified=" + this.getLastModified() + ")";
    }

    @Generated
    public SpaceWithStatisticResult(long id, String key, String name, String spaceType, String status, Long pageBlogDraftCount, Long attachmentSize, Long attachmentCount, Long teamCalendarCount, Long estimatedMigrationTime, Timestamp lastModified) {
        this.id = id;
        this.key = key;
        this.name = name;
        this.spaceType = spaceType;
        this.status = status;
        this.pageBlogDraftCount = pageBlogDraftCount;
        this.attachmentSize = attachmentSize;
        this.attachmentCount = attachmentCount;
        this.teamCalendarCount = teamCalendarCount;
        this.estimatedMigrationTime = estimatedMigrationTime;
        this.lastModified = lastModified;
    }
}

