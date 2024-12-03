/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.upgrade.impl;

import com.atlassian.confluence.cluster.shareddata.SharedDataManager;
import com.atlassian.confluence.upgrade.UpgradeGate;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultUpgradeGate
implements UpgradeGate {
    private static final String UPGRADE_REQUIRED_KEY = "cluster.upgrade.required";
    private static final String PLUGIN_DEPENDENT_UPGRADE_FINISHED = "cluster.upgrade.plugin.dependent.finished";
    private static final int UPGRADE_REQUIRED_CHECK_ATTEMPTS = 90;
    private static final int UPGRADE_REQUIRED_CHECK_SLEEP = 1000;
    private static final int WAIT_FOR_OTHER_NODE_UPGRADE_ATTEMPTS = 60;
    private static final int WAIT_FOR_OTHER_NODE_UPGRADE_SLEEP = 30000;
    private static final Logger LOG = LoggerFactory.getLogger(DefaultUpgradeGate.class);
    private final SharedDataManager clusterSharedDataManager;

    private DefaultUpgradeGate(SharedDataManager clusterSharedDataManager) {
        this.clusterSharedDataManager = clusterSharedDataManager;
    }

    @Override
    public void setUpgradeRequired(boolean required) {
        this.getStateMap().put(UPGRADE_REQUIRED_KEY, required);
    }

    @Override
    public void setPluginDependentUpgradeComplete(boolean complete) {
        this.getStateMap().put(PLUGIN_DEPENDENT_UPGRADE_FINISHED, complete);
    }

    private Map<String, Boolean> getStateMap() {
        return this.clusterSharedDataManager.getSharedData(this.getClass().getName()).getMap();
    }

    @Override
    public boolean isUpgradeRequiredWithWait() {
        return this.waitForBooleanValue(UPGRADE_REQUIRED_KEY, "plugin dependent upgrades are required", 1000, 90);
    }

    @Override
    public boolean isPluginDependentUpgradeCompleteWithWait() {
        return this.waitForBooleanValue(PLUGIN_DEPENDENT_UPGRADE_FINISHED, "plugin dependent upgrades are complete", 30000, 60);
    }

    private boolean waitForBooleanValue(String valueName, String taskNameForLogging, int sleepTimeMs, int attempts) {
        LOG.info("Waiting to find if {}. Maximum wait time will be {} seconds.", (Object)taskNameForLogging, (Object)(sleepTimeMs / 1000 * attempts));
        Boolean requiredObj = this.getStateMap().get(valueName);
        for (int counter = 1; requiredObj == null && counter <= attempts; ++counter) {
            if (counter % 10 == 0) {
                LOG.info("Still waiting to find if {}. Remaining wait time is {} seconds.", (Object)taskNameForLogging, (Object)(sleepTimeMs / 1000 * (attempts - counter)));
            }
            try {
                Thread.sleep(sleepTimeMs);
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
            requiredObj = this.getStateMap().get(valueName);
        }
        if (requiredObj == null) {
            LOG.info("Timed out waiting to find if {}. Default to false.", (Object)taskNameForLogging);
            requiredObj = Boolean.FALSE;
        }
        boolean required = requiredObj;
        LOG.info("{} : {}.", (Object)taskNameForLogging, (Object)required);
        return required;
    }
}

