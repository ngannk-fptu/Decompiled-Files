/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.analytics.events.EventDto
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.base.Checker
 *  com.atlassian.cmpt.check.mapper.CheckResultMapper
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.migration.agent.service.check.maintenance;

import com.atlassian.cmpt.analytics.events.EventDto;
import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.cmpt.check.mapper.CheckResultMapper;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.check.CheckContextProvider;
import com.atlassian.migration.agent.service.check.CheckRegistration;
import com.atlassian.migration.agent.service.check.CheckType;
import com.atlassian.migration.agent.service.check.maintenance.MigrationOrchestratorMaintenanceCheckContextProvider;
import com.atlassian.migration.agent.service.check.maintenance.MigrationOrchestratorMaintenanceChecker;
import com.atlassian.migration.agent.service.check.maintenance.MigrationOrchestratorMaintenanceContext;
import com.atlassian.migration.agent.service.check.maintenance.MigrationOrchestratorMaintenanceMapper;
import com.atlassian.migration.agent.service.cloud.CloudSiteService;
import com.atlassian.migration.agent.service.mo.MigrationOrchestratorClient;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class MigrationOrchestratorMaintenanceCheckRegistration
implements CheckRegistration<MigrationOrchestratorMaintenanceContext> {
    private final MigrationOrchestratorMaintenanceChecker migrationOrchestratorMaintenanceChecker;
    private final MigrationOrchestratorMaintenanceMapper migrationOrchestratorMaintenanceMapper;
    private final MigrationOrchestratorMaintenanceCheckContextProvider migrationOrchestratorMaintenanceCheckContextProvider;
    private final AnalyticsEventBuilder analyticsEventBuilder;

    public MigrationOrchestratorMaintenanceCheckRegistration(CloudSiteService cloudSiteService, MigrationOrchestratorClient migrationOrchestratorClient, AnalyticsEventBuilder analyticsEventBuilder) {
        this.migrationOrchestratorMaintenanceChecker = new MigrationOrchestratorMaintenanceChecker(cloudSiteService, migrationOrchestratorClient);
        this.migrationOrchestratorMaintenanceCheckContextProvider = new MigrationOrchestratorMaintenanceCheckContextProvider();
        this.migrationOrchestratorMaintenanceMapper = new MigrationOrchestratorMaintenanceMapper();
        this.analyticsEventBuilder = analyticsEventBuilder;
    }

    @Override
    public CheckType getCheckType() {
        return CheckType.MIGRATION_ORCHESTRATOR_MAINTENANCE;
    }

    @Override
    public Checker<MigrationOrchestratorMaintenanceContext> getChecker() {
        return this.migrationOrchestratorMaintenanceChecker;
    }

    @Override
    public CheckContextProvider<MigrationOrchestratorMaintenanceContext> getCheckContextProvider() {
        return this.migrationOrchestratorMaintenanceCheckContextProvider;
    }

    @Override
    public CheckResultMapper getCheckResultMapper() {
        return this.migrationOrchestratorMaintenanceMapper;
    }

    @Override
    public EventDto getAnalyticsEventModel(CheckResult checkResult, long totalTime) {
        return this.analyticsEventBuilder.buildPreFlightMigrationOrchestratorMaintenance(checkResult.success, totalTime);
    }

    @Override
    public String getFailedToExecuteAnalyticsEventName() {
        return "migrationOrchestratorMaintenanceCheck";
    }
}

