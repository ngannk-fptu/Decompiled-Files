/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Column
 *  javax.persistence.Entity
 *  javax.persistence.Id
 *  javax.persistence.Table
 *  lombok.Generated
 */
package com.atlassian.migration.agent.entity;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Generated;

@Entity
@Table(name="MIG_SPACE_STATISTIC")
public class SpaceStatistic {
    @Id
    @Column(name="spaceId", nullable=false)
    private long spaceId;
    @Column(name="attachmentSize", nullable=false)
    private long attachmentSize;
    @Column(name="attachmentCount", nullable=false)
    private long attachmentCount;
    @Column(name="sumOfPageBlogDraftCount", nullable=false)
    private long sumOfPageBlogDraftCount;
    @Column(name="teamCalendarCount")
    private Long teamCalendarCount;
    @Column(name="lastUpdated")
    private Instant lastUpdated;
    @Column(name="estimatedMigrationTime", nullable=false)
    private long estimatedMigrationTime;
    @Column(name="lastCalculated", nullable=false)
    private Instant lastCalculated;

    public SpaceStatistic(long spaceId, long sumOfPageBlogDraftCount, long attachmentCount, Long teamCalendarCount, long attachmentSize, Instant lastUpdated, Instant lastCalculated, long estimatedMigrationTime) {
        this.spaceId = spaceId;
        this.sumOfPageBlogDraftCount = sumOfPageBlogDraftCount;
        this.attachmentCount = attachmentCount;
        this.teamCalendarCount = teamCalendarCount;
        this.attachmentSize = attachmentSize;
        this.lastUpdated = lastUpdated;
        this.estimatedMigrationTime = estimatedMigrationTime;
        this.lastCalculated = lastCalculated;
    }

    @Generated
    public SpaceStatistic() {
    }

    @Generated
    public long getSpaceId() {
        return this.spaceId;
    }

    @Generated
    public long getAttachmentSize() {
        return this.attachmentSize;
    }

    @Generated
    public long getAttachmentCount() {
        return this.attachmentCount;
    }

    @Generated
    public long getSumOfPageBlogDraftCount() {
        return this.sumOfPageBlogDraftCount;
    }

    @Generated
    public Long getTeamCalendarCount() {
        return this.teamCalendarCount;
    }

    @Generated
    public Instant getLastUpdated() {
        return this.lastUpdated;
    }

    @Generated
    public long getEstimatedMigrationTime() {
        return this.estimatedMigrationTime;
    }

    @Generated
    public Instant getLastCalculated() {
        return this.lastCalculated;
    }

    @Generated
    public void setSpaceId(long spaceId) {
        this.spaceId = spaceId;
    }

    @Generated
    public void setAttachmentSize(long attachmentSize) {
        this.attachmentSize = attachmentSize;
    }

    @Generated
    public void setAttachmentCount(long attachmentCount) {
        this.attachmentCount = attachmentCount;
    }

    @Generated
    public void setSumOfPageBlogDraftCount(long sumOfPageBlogDraftCount) {
        this.sumOfPageBlogDraftCount = sumOfPageBlogDraftCount;
    }

    @Generated
    public void setTeamCalendarCount(Long teamCalendarCount) {
        this.teamCalendarCount = teamCalendarCount;
    }

    @Generated
    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Generated
    public void setEstimatedMigrationTime(long estimatedMigrationTime) {
        this.estimatedMigrationTime = estimatedMigrationTime;
    }

    @Generated
    public void setLastCalculated(Instant lastCalculated) {
        this.lastCalculated = lastCalculated;
    }
}

