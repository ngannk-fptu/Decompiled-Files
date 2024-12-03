/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.stepexecutor.space;

import com.atlassian.migration.agent.service.stepexecutor.space.TombstoneUser;
import java.util.List;
import lombok.Generated;

public class PublishTombstoneMappingRequest {
    private String migrationScopeId;
    private List<TombstoneUser> tombstoneUsers;

    @Generated
    public String getMigrationScopeId() {
        return this.migrationScopeId;
    }

    @Generated
    public List<TombstoneUser> getTombstoneUsers() {
        return this.tombstoneUsers;
    }

    @Generated
    public void setMigrationScopeId(String migrationScopeId) {
        this.migrationScopeId = migrationScopeId;
    }

    @Generated
    public void setTombstoneUsers(List<TombstoneUser> tombstoneUsers) {
        this.tombstoneUsers = tombstoneUsers;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PublishTombstoneMappingRequest)) {
            return false;
        }
        PublishTombstoneMappingRequest other = (PublishTombstoneMappingRequest)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$migrationScopeId = this.getMigrationScopeId();
        String other$migrationScopeId = other.getMigrationScopeId();
        if (this$migrationScopeId == null ? other$migrationScopeId != null : !this$migrationScopeId.equals(other$migrationScopeId)) {
            return false;
        }
        List<TombstoneUser> this$tombstoneUsers = this.getTombstoneUsers();
        List<TombstoneUser> other$tombstoneUsers = other.getTombstoneUsers();
        return !(this$tombstoneUsers == null ? other$tombstoneUsers != null : !((Object)this$tombstoneUsers).equals(other$tombstoneUsers));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof PublishTombstoneMappingRequest;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $migrationScopeId = this.getMigrationScopeId();
        result = result * 59 + ($migrationScopeId == null ? 43 : $migrationScopeId.hashCode());
        List<TombstoneUser> $tombstoneUsers = this.getTombstoneUsers();
        result = result * 59 + ($tombstoneUsers == null ? 43 : ((Object)$tombstoneUsers).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "PublishTombstoneMappingRequest(migrationScopeId=" + this.getMigrationScopeId() + ", tombstoneUsers=" + this.getTombstoneUsers() + ")";
    }

    @Generated
    public PublishTombstoneMappingRequest(String migrationScopeId, List<TombstoneUser> tombstoneUsers) {
        this.migrationScopeId = migrationScopeId;
        this.tombstoneUsers = tombstoneUsers;
    }
}

