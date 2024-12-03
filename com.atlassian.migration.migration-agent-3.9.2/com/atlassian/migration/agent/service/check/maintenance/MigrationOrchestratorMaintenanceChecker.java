/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.base.Checker
 *  javax.inject.Inject
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.service.check.maintenance;

import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.migration.agent.entity.CloudSite;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.service.PreflightErrorCode;
import com.atlassian.migration.agent.service.check.maintenance.MigrationOrchestratorMaintenanceContext;
import com.atlassian.migration.agent.service.cloud.CloudSiteService;
import com.atlassian.migration.agent.service.mo.MigrationOrchestratorClient;
import com.atlassian.migration.agent.service.mo.MigrationOrchestratorUtils;
import java.util.Optional;
import javax.inject.Inject;
import org.slf4j.Logger;

public class MigrationOrchestratorMaintenanceChecker
implements Checker<MigrationOrchestratorMaintenanceContext> {
    private static final Logger log = ContextLoggerFactory.getLogger(MigrationOrchestratorMaintenanceChecker.class);
    private final CloudSiteService cloudSiteService;
    private final MigrationOrchestratorClient migrationOrchestratorClient;

    @Inject
    public MigrationOrchestratorMaintenanceChecker(CloudSiteService cloudSiteService, MigrationOrchestratorClient migrationOrchestratorClient) {
        this.cloudSiteService = cloudSiteService;
        this.migrationOrchestratorClient = migrationOrchestratorClient;
    }

    public CheckResult check(MigrationOrchestratorMaintenanceContext ctx) {
        Optional<CloudSite> cloudSite = this.cloudSiteService.getByCloudId(ctx.cloudId);
        PreflightErrorCode cloudErrorCode = PreflightErrorCode.CLOUD_ERROR;
        if (!cloudSite.isPresent()) {
            log.error("Cannot find CloudSite using cloudId: {}.", (Object)ctx.cloudId);
            return Checker.buildCheckResultWithExecutionError((int)cloudErrorCode.getCode());
        }
        String containerToken = cloudSite.get().getContainerToken();
        try {
            boolean isInMaintenance = this.migrationOrchestratorClient.isInMaintenance(containerToken);
            return new CheckResult(!isInMaintenance);
        }
        catch (Exception e) {
            if (MigrationOrchestratorUtils.isAuthorizationCause(e)) {
                return new CheckResult(true);
            }
            log.error("Error during migration orchestrator maintenance check", (Throwable)e);
            return Checker.buildCheckResultWithExecutionError((int)PreflightErrorCode.MIGRATION_ORCHESTRATOR_MAINTENANCE_CHECK_ERROR.getCode());
        }
    }
}

