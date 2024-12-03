/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckContext
 */
package com.atlassian.migration.agent.service.check.app.vendorcheck;

import com.atlassian.cmpt.check.base.CheckContext;
import java.util.Set;

public class AppVendorCheckContext
implements CheckContext {
    public final String planId;
    public final String planName;
    public final String planMigrationTag;
    public final String cloudId;
    public final Set<String> spaceKeys;
    public final Set<String> appKeys;

    public AppVendorCheckContext(String planId, String planName, String planMigrationTag, String cloudId, Set<String> spaceKeys, Set<String> appKeys) {
        this.planId = planId;
        this.planName = planName;
        this.planMigrationTag = planMigrationTag;
        this.cloudId = cloudId;
        this.spaceKeys = spaceKeys;
        this.appKeys = appKeys;
    }
}

