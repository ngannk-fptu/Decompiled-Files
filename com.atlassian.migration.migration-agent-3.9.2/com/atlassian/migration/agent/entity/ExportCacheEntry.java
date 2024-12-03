/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Column
 *  javax.persistence.Entity
 *  javax.persistence.EnumType
 *  javax.persistence.Enumerated
 *  javax.persistence.Table
 */
package com.atlassian.migration.agent.entity;

import com.atlassian.migration.agent.entity.ExportType;
import com.atlassian.migration.agent.entity.WithId;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Entity
@Table(name="MIG_EXPORT_CACHE")
public class ExportCacheEntry
extends WithId {
    @Column(name="snapshotTime", nullable=false)
    private long snapshotTime;
    @Column(name="exportType", nullable=false)
    @Enumerated(value=EnumType.STRING)
    private ExportType exportType;
    @Column(name="spaceKey", nullable=false)
    private String spaceKey;
    @Column(name="cloudId", nullable=false)
    private String cloudId;
    @Column(name="containsUserMigrationTask", nullable=false)
    private boolean containsUserMigrationTask;
    @Column(name="filePath", nullable=false)
    private String filePath;
    @Column(name="bandanaHash", nullable=false)
    private String bandanaHash;
    @Column(name="osPropertyEntryHash", nullable=false)
    private String osPropertyEntryHash;
    @Column(name="userMappingHash", nullable=false)
    private String userMappingHash;

    public ExportCacheEntry() {
    }

    public ExportCacheEntry(long snapshotTime, ExportType exportType, String spaceKey, String cloudId, boolean containsUserMigrationTask, String filePath, String bandanaHash, String osPropertyEntryHash, String userMappingHash) {
        this.snapshotTime = snapshotTime;
        this.exportType = exportType;
        this.spaceKey = spaceKey;
        this.cloudId = cloudId;
        this.containsUserMigrationTask = containsUserMigrationTask;
        this.filePath = filePath;
        this.bandanaHash = bandanaHash;
        this.osPropertyEntryHash = osPropertyEntryHash;
        this.userMappingHash = userMappingHash;
    }

    public long getSnapshotTime() {
        return this.snapshotTime;
    }

    public void setSnapshotTime(long snapshotTime) {
        this.snapshotTime = snapshotTime;
    }

    public ExportType getExportType() {
        return this.exportType;
    }

    public void setExportType(ExportType exportType) {
        this.exportType = exportType;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }

    public String getCloudId() {
        return this.cloudId;
    }

    public void setCloudId(String cloudId) {
        this.cloudId = cloudId;
    }

    public boolean isContainsUserMigrationTask() {
        return this.containsUserMigrationTask;
    }

    public boolean containsUserMigrationTask() {
        return this.containsUserMigrationTask;
    }

    public void setContainsUserMigrationTask(boolean containsUserMigrationTask) {
        this.containsUserMigrationTask = containsUserMigrationTask;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getBandanaHash() {
        return this.bandanaHash;
    }

    public void setBandanaHash(String bandanaHash) {
        this.bandanaHash = bandanaHash;
    }

    public String getOsPropertyEntryHash() {
        return this.osPropertyEntryHash;
    }

    public void setOsPropertyEntryHash(String osPropertyEntryHash) {
        this.osPropertyEntryHash = osPropertyEntryHash;
    }

    public String getUserMappingHash() {
        return this.userMappingHash;
    }

    public void setUserMappingHash(String userMappingHash) {
        this.userMappingHash = userMappingHash;
    }
}

