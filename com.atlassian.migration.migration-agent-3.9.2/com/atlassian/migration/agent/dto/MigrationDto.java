/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.migration.utils.MigrationStatusCalculator$CoreMigrationStatus
 *  com.atlassian.migration.utils.MigrationStatusCalculator$OverallAppMigrationStatus
 *  com.atlassian.migration.utils.MigrationStatusCalculator$OverallMigrationStatus
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.dto;

import com.atlassian.migration.utils.MigrationStatusCalculator;
import org.codehaus.jackson.annotate.JsonProperty;

public class MigrationDto {
    @JsonProperty
    private MigrationStatusCalculator.OverallMigrationStatus overallStatus;
    @JsonProperty
    private MigrationStatusCalculator.CoreMigrationStatus coreStatus;
    @JsonProperty
    private MigrationStatusCalculator.OverallAppMigrationStatus appStatus;

    public MigrationDto(MigrationStatusCalculator.OverallMigrationStatus overallStatus, MigrationStatusCalculator.CoreMigrationStatus coreStatus, MigrationStatusCalculator.OverallAppMigrationStatus appStatus) {
        this.overallStatus = overallStatus;
        this.coreStatus = coreStatus;
        this.appStatus = appStatus;
    }

    public MigrationStatusCalculator.OverallMigrationStatus getOverallStatus() {
        return this.overallStatus;
    }

    public MigrationStatusCalculator.CoreMigrationStatus getCoreStatus() {
        return this.coreStatus;
    }

    public MigrationStatusCalculator.OverallAppMigrationStatus getAppStatus() {
        return this.appStatus;
    }
}

