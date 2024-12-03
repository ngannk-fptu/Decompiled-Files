/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.lifecycle.LifecycleContext
 *  com.atlassian.config.lifecycle.LifecycleItem
 *  com.atlassian.confluence.upgrade.DeferredUpgradeTask
 */
package com.atlassian.confluence.upgrade;

import com.atlassian.config.lifecycle.LifecycleContext;
import com.atlassian.config.lifecycle.LifecycleItem;
import com.atlassian.confluence.upgrade.DeferredUpgradeTask;
import com.atlassian.confluence.upgrade.UpgradeGate;
import com.atlassian.confluence.upgrade.impl.DefaultUpgradeManager;
import java.util.ArrayList;
import java.util.List;

public class PluginFrameworkDependentUpgrader
implements LifecycleItem {
    private UpgradeGate upgradeGate;
    private DefaultUpgradeManager upgradeManager;

    public void startup(LifecycleContext context) throws Exception {
        if (!this.upgradeGate.isUpgradeRequiredWithWait()) {
            return;
        }
        this.runDeferredUpgradeTasks();
        this.upgradeManager.entireUpgradeFinished();
    }

    private void runDeferredUpgradeTasks() throws Exception {
        List tasks = this.upgradeManager.getPluginDependentUpgradeTasks();
        if (tasks.isEmpty()) {
            return;
        }
        ArrayList<DeferredUpgradeTask> requiredTasks = new ArrayList<DeferredUpgradeTask>();
        for (DeferredUpgradeTask task : tasks) {
            if (!task.isUpgradeRequired()) continue;
            requiredTasks.add(task);
        }
        if (requiredTasks.isEmpty()) {
            this.upgradeGate.isPluginDependentUpgradeCompleteWithWait();
        } else {
            for (DeferredUpgradeTask task : requiredTasks) {
                task.doDeferredUpgrade();
            }
            this.upgradeGate.setPluginDependentUpgradeComplete(true);
        }
    }

    public void shutdown(LifecycleContext context) throws Exception {
    }

    public void setUpgradeManager(DefaultUpgradeManager upgradeManager) {
        this.upgradeManager = upgradeManager;
    }

    public void setUpgradeGate(UpgradeGate upgradeGate) {
        this.upgradeGate = upgradeGate;
    }
}

