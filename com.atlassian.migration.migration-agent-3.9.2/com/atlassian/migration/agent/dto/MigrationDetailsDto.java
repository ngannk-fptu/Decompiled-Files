/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.dto;

import javax.annotation.Nullable;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonProperty;

public class MigrationDetailsDto {
    @JsonProperty
    @Nullable
    public String migrationId;
    @JsonProperty
    @Nullable
    public String migrationScopeId;
    @JsonProperty
    @Nullable
    public String planId;
    @JsonProperty
    @Nullable
    public String cloudId;
    @JsonProperty
    @Nullable
    public String stepId;

    @Nullable
    @Generated
    public String getMigrationId() {
        return this.migrationId;
    }

    @Nullable
    @Generated
    public String getMigrationScopeId() {
        return this.migrationScopeId;
    }

    @Nullable
    @Generated
    public String getPlanId() {
        return this.planId;
    }

    @Nullable
    @Generated
    public String getCloudId() {
        return this.cloudId;
    }

    @Nullable
    @Generated
    public String getStepId() {
        return this.stepId;
    }

    @Generated
    public void setMigrationId(@Nullable String migrationId) {
        this.migrationId = migrationId;
    }

    @Generated
    public void setMigrationScopeId(@Nullable String migrationScopeId) {
        this.migrationScopeId = migrationScopeId;
    }

    @Generated
    public void setPlanId(@Nullable String planId) {
        this.planId = planId;
    }

    @Generated
    public void setCloudId(@Nullable String cloudId) {
        this.cloudId = cloudId;
    }

    @Generated
    public void setStepId(@Nullable String stepId) {
        this.stepId = stepId;
    }

    @Generated
    public MigrationDetailsDto() {
    }

    @Generated
    public MigrationDetailsDto(@Nullable String migrationId, @Nullable String migrationScopeId, @Nullable String planId, @Nullable String cloudId, @Nullable String stepId) {
        this.migrationId = migrationId;
        this.migrationScopeId = migrationScopeId;
        this.planId = planId;
        this.cloudId = cloudId;
        this.stepId = stepId;
    }
}

