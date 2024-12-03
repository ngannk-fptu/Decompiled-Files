/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.migration.agent.service.check.maintenance;

import com.atlassian.migration.agent.service.check.CheckContextProvider;
import com.atlassian.migration.agent.service.check.ContextProviderUtil;
import com.atlassian.migration.agent.service.check.maintenance.MigrationOrchestratorMaintenanceContext;
import java.util.Map;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class MigrationOrchestratorMaintenanceCheckContextProvider
implements CheckContextProvider<MigrationOrchestratorMaintenanceContext> {
    @Override
    public MigrationOrchestratorMaintenanceContext apply(Map<String, Object> parameters) {
        String cloudId = ContextProviderUtil.getCloudId(parameters);
        return new MigrationOrchestratorMaintenanceContext(cloudId);
    }
}

