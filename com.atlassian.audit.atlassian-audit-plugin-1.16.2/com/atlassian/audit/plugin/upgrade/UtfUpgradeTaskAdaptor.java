/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.upgrade.PluginUpgradeTask
 *  com.atlassian.upgrade.api.UpgradeContext
 *  com.atlassian.upgrade.spi.UpgradeTask
 */
package com.atlassian.audit.plugin.upgrade;

import com.atlassian.sal.api.upgrade.PluginUpgradeTask;
import com.atlassian.upgrade.api.UpgradeContext;
import com.atlassian.upgrade.spi.UpgradeTask;
import java.util.Objects;

public class UtfUpgradeTaskAdaptor
implements UpgradeTask {
    private final PluginUpgradeTask pluginUpgradeTask;

    UtfUpgradeTaskAdaptor(PluginUpgradeTask pluginUpgradeTask) {
        this.pluginUpgradeTask = Objects.requireNonNull(pluginUpgradeTask);
    }

    public int getBuildNumber() {
        return this.pluginUpgradeTask.getBuildNumber();
    }

    public String getShortDescription() {
        return this.pluginUpgradeTask.getShortDescription();
    }

    public void runUpgrade(UpgradeContext upgradeContext) {
        try {
            this.pluginUpgradeTask.doUpgrade();
        }
        catch (Exception e) {
            throw new RuntimeException("Error running update task", e);
        }
    }
}

