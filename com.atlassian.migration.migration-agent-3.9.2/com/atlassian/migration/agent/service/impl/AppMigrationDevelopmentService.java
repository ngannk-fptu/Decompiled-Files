/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.migration.app.dto.AppContainerDetails
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.service.impl;

import com.atlassian.migration.agent.entity.Plan;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.service.catalogue.MigrationDetails;
import com.atlassian.migration.agent.service.catalogue.PlatformService;
import com.atlassian.migration.agent.store.PlanStore;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import com.atlassian.migration.app.DefaultRegistrar;
import com.atlassian.migration.app.dto.AppContainerDetails;
import java.util.Set;
import javax.annotation.ParametersAreNonnullByDefault;
import org.slf4j.Logger;

@ParametersAreNonnullByDefault
public class AppMigrationDevelopmentService {
    private final PluginTransactionTemplate ptx;
    private final DefaultRegistrar registrar;
    private final PlanStore planStore;
    private final PlatformService platformService;
    private static final Logger log = ContextLoggerFactory.getLogger(AppMigrationDevelopmentService.class);

    public AppMigrationDevelopmentService(PluginTransactionTemplate ptx, DefaultRegistrar registrar, PlanStore planStore, PlatformService platformService) {
        this.ptx = ptx;
        this.registrar = registrar;
        this.planStore = planStore;
        this.platformService = platformService;
    }

    public void rerunAppMigrationForPlan(String planId) {
        this.rerunAppMigrationForPlan(planId, null);
    }

    public void rerunAppMigrationForPlan(String planId, Set<String> appKeys) {
        log.info("Re-running App migration for planId: {}", (Object)planId);
        this.ptx.read(() -> {
            Plan plan = this.planStore.getPlan(planId);
            MigrationDetails migrationDetails = this.platformService.publishMigrationDetailsForAllListeners(plan);
            String cloudId = plan.getCloudSite().getCloudId();
            String migrationId = migrationDetails.migrationId;
            this.platformService.createContainersInMcs(cloudId, migrationId, plan);
            this.platformService.createAppContainers(cloudId, migrationId, appKeys);
            Set<AppContainerDetails> appContainerDetails = this.platformService.getAppContainers(cloudId, migrationId);
            this.registrar.startMigration(cloudId, migrationId, appContainerDetails);
        });
    }
}

