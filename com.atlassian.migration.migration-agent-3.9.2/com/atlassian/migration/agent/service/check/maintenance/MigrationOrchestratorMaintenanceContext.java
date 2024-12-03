/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckContext
 */
package com.atlassian.migration.agent.service.check.maintenance;

import com.atlassian.cmpt.check.base.CheckContext;

public class MigrationOrchestratorMaintenanceContext
implements CheckContext {
    public final String cloudId;

    public MigrationOrchestratorMaintenanceContext(String cloudId) {
        this.cloudId = cloudId;
    }
}

