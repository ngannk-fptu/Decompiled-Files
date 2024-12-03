/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.migration.app.dto.TransferLogEnablement
 *  com.atlassian.migration.app.dto.TransferLogResponse
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.service.impl;

import com.atlassian.migration.agent.entity.Plan;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.store.PlanStore;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import com.atlassian.migration.app.DefaultAppMigrationServiceClient;
import com.atlassian.migration.app.dto.TransferLogEnablement;
import com.atlassian.migration.app.dto.TransferLogResponse;
import org.slf4j.Logger;

public class AppTransferLogService {
    private final PluginTransactionTemplate ptx;
    private final PlanStore planStore;
    private final DefaultAppMigrationServiceClient appMigrationServiceClient;
    private static final Logger log = ContextLoggerFactory.getLogger(AppTransferLogService.class);

    public AppTransferLogService(PluginTransactionTemplate ptx, DefaultAppMigrationServiceClient appMigrationServiceClient, PlanStore planStore) {
        this.appMigrationServiceClient = appMigrationServiceClient;
        this.planStore = planStore;
        this.ptx = ptx;
    }

    public TransferLogResponse getTransferLogResponse(String planId, String containerId) {
        log.debug("Getting transfer logs for containerId: {} planId: {}", (Object)containerId, (Object)planId);
        return this.ptx.read(() -> {
            Plan plan = this.planStore.getPlan(planId);
            String cloudId = plan.getCloudSite().getCloudId();
            TransferLogResponse response = this.appMigrationServiceClient.getTransferLogResponse(cloudId, containerId);
            log.debug("Finished getting transfer logs with content disposition: {}", (Object)response.getContentDispositionHeader());
            return response;
        });
    }

    public TransferLogEnablement isTransferLogsEnabled(String planId, String containerId) {
        log.debug("Checking if transfer logs enabled for containerId: {} planId: {}", (Object)containerId, (Object)planId);
        return this.ptx.read(() -> {
            Plan plan = this.planStore.getPlan(planId);
            String cloudId = plan.getCloudSite().getCloudId();
            TransferLogEnablement response = this.appMigrationServiceClient.isTransferLogsEnabled(cloudId, containerId);
            log.debug("Transfer logs enabled: {} with blockers: {}", (Object)response.getEnabled(), (Object)response.getBlockers());
            return response;
        });
    }
}

