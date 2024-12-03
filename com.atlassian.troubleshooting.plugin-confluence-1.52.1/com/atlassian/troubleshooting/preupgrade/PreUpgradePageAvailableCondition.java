/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.web.Condition
 */
package com.atlassian.troubleshooting.preupgrade;

import com.atlassian.plugin.web.Condition;
import com.atlassian.troubleshooting.preupgrade.PreUpgradePlanningManager;
import java.util.Map;
import java.util.Objects;

public class PreUpgradePageAvailableCondition
implements Condition {
    private PreUpgradePlanningManager preUpgradePlanningManager;

    public void init(Map<String, String> map) {
    }

    public boolean shouldDisplay(Map<String, Object> map) {
        return this.preUpgradePlanningManager.isPreUpgradePageAvailable();
    }

    public void setPreUpgradePlanningManager(PreUpgradePlanningManager preUpgradePlanningManager) {
        this.preUpgradePlanningManager = Objects.requireNonNull(preUpgradePlanningManager);
    }
}

