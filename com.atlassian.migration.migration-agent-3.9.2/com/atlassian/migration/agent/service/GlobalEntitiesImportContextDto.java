/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service;

import com.atlassian.migration.agent.service.catalogue.model.MigrationCatalogueStorageFile;
import java.util.List;
import lombok.Generated;

public class GlobalEntitiesImportContextDto {
    private final String planId;
    private final String taskId;
    private final String migrationScopeId;
    private final String migrationId;
    private final List<MigrationCatalogueStorageFile> files;

    @Generated
    public GlobalEntitiesImportContextDto(String planId, String taskId, String migrationScopeId, String migrationId, List<MigrationCatalogueStorageFile> files) {
        this.planId = planId;
        this.taskId = taskId;
        this.migrationScopeId = migrationScopeId;
        this.migrationId = migrationId;
        this.files = files;
    }

    @Generated
    public String getPlanId() {
        return this.planId;
    }

    @Generated
    public String getTaskId() {
        return this.taskId;
    }

    @Generated
    public String getMigrationScopeId() {
        return this.migrationScopeId;
    }

    @Generated
    public String getMigrationId() {
        return this.migrationId;
    }

    @Generated
    public List<MigrationCatalogueStorageFile> getFiles() {
        return this.files;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof GlobalEntitiesImportContextDto)) {
            return false;
        }
        GlobalEntitiesImportContextDto other = (GlobalEntitiesImportContextDto)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$planId = this.getPlanId();
        String other$planId = other.getPlanId();
        if (this$planId == null ? other$planId != null : !this$planId.equals(other$planId)) {
            return false;
        }
        String this$taskId = this.getTaskId();
        String other$taskId = other.getTaskId();
        if (this$taskId == null ? other$taskId != null : !this$taskId.equals(other$taskId)) {
            return false;
        }
        String this$migrationScopeId = this.getMigrationScopeId();
        String other$migrationScopeId = other.getMigrationScopeId();
        if (this$migrationScopeId == null ? other$migrationScopeId != null : !this$migrationScopeId.equals(other$migrationScopeId)) {
            return false;
        }
        String this$migrationId = this.getMigrationId();
        String other$migrationId = other.getMigrationId();
        if (this$migrationId == null ? other$migrationId != null : !this$migrationId.equals(other$migrationId)) {
            return false;
        }
        List<MigrationCatalogueStorageFile> this$files = this.getFiles();
        List<MigrationCatalogueStorageFile> other$files = other.getFiles();
        return !(this$files == null ? other$files != null : !((Object)this$files).equals(other$files));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof GlobalEntitiesImportContextDto;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $planId = this.getPlanId();
        result = result * 59 + ($planId == null ? 43 : $planId.hashCode());
        String $taskId = this.getTaskId();
        result = result * 59 + ($taskId == null ? 43 : $taskId.hashCode());
        String $migrationScopeId = this.getMigrationScopeId();
        result = result * 59 + ($migrationScopeId == null ? 43 : $migrationScopeId.hashCode());
        String $migrationId = this.getMigrationId();
        result = result * 59 + ($migrationId == null ? 43 : $migrationId.hashCode());
        List<MigrationCatalogueStorageFile> $files = this.getFiles();
        result = result * 59 + ($files == null ? 43 : ((Object)$files).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "GlobalEntitiesImportContextDto(planId=" + this.getPlanId() + ", taskId=" + this.getTaskId() + ", migrationScopeId=" + this.getMigrationScopeId() + ", migrationId=" + this.getMigrationId() + ", files=" + this.getFiles() + ")";
    }
}

