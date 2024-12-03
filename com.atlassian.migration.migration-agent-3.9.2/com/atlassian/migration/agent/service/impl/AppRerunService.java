/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.migration.app.dto.RerunEnablementDto
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.service.impl;

import com.atlassian.migration.agent.entity.Plan;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.store.PlanStore;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import com.atlassian.migration.app.AbstractCloudMigrationRegistrar;
import com.atlassian.migration.app.dto.RerunEnablementDto;
import org.slf4j.Logger;

public class AppRerunService {
    private final PluginTransactionTemplate ptx;
    private final AbstractCloudMigrationRegistrar registrar;
    private final PlanStore planStore;
    private static final Logger log = ContextLoggerFactory.getLogger(AppRerunService.class);

    public AppRerunService(PluginTransactionTemplate ptx, AbstractCloudMigrationRegistrar registrar, PlanStore planStore) {
        this.ptx = ptx;
        this.registrar = registrar;
        this.planStore = planStore;
    }

    public RerunEnablementDto isRerunEnabled(String planId, String containerId, String serverAppKey) {
        log.debug("Checking isRerunEnabled for containerId: {} planId: {}", (Object)containerId, (Object)planId);
        return this.ptx.read(() -> {
            Plan plan = this.planStore.getPlan(planId);
            String cloudId = plan.getCloudSite().getCloudId();
            return this.registrar.isRerunEnabled(cloudId, containerId, serverAppKey);
        });
    }

    public void rerunAppMigration(String planId, String containerId, String serverAppKey) {
        log.debug("Re-running App Migration for containerId: {} planId: {}", (Object)containerId, (Object)planId);
        this.ptx.write(() -> {
            Plan plan = this.planStore.getPlan(planId);
            String cloudId = plan.getCloudSite().getCloudId();
            this.registrar.rerunMigration(cloudId, plan.getMigrationId(), containerId, serverAppKey);
        });
    }
}

