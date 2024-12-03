/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.confluence.cluster.ZduManager
 *  com.atlassian.confluence.cluster.ZduStatus$State
 *  com.atlassian.confluence.event.events.cluster.ZduFinalizationRequestEvent
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.PluginEventListener
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.sal.api.upgrade.PluginUpgradeTask
 *  com.atlassian.sal.core.upgrade.DefaultPluginUpgradeManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.sal.confluence.lifecycle;

import com.atlassian.beehive.ClusterLockService;
import com.atlassian.confluence.cluster.ZduManager;
import com.atlassian.confluence.cluster.ZduStatus;
import com.atlassian.confluence.event.events.cluster.ZduFinalizationRequestEvent;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.upgrade.PluginUpgradeTask;
import com.atlassian.sal.core.upgrade.DefaultPluginUpgradeManager;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultPluginUpgradeFinalizationManager
extends DefaultPluginUpgradeManager {
    private static final Logger logger = LoggerFactory.getLogger(DefaultPluginUpgradeFinalizationManager.class);
    private final ZduManager zduManager;

    public DefaultPluginUpgradeFinalizationManager(List<PluginUpgradeTask> upgradeTasks, TransactionTemplate transactionTemplate, PluginAccessor pluginAccessor, PluginSettingsFactory pluginSettingsFactory, PluginEventManager pluginEventManager, ClusterLockService clusterLockService, ZduManager zduManager) {
        super(upgradeTasks, transactionTemplate, pluginAccessor, pluginSettingsFactory, pluginEventManager, clusterLockService, ":build:finalized");
        this.zduManager = zduManager;
    }

    public List<Message> upgradeInternal() {
        if (this.shouldRunUpgradeTasks()) {
            return super.upgradeInternal();
        }
        logger.info("ZDU mode enabled: omit finalization tasks");
        return Collections.emptyList();
    }

    public List<Message> upgradeInternal(Plugin plugin) {
        if (this.shouldRunUpgradeTasks()) {
            return super.upgradeInternal(plugin);
        }
        logger.info("ZDU mode enabled: omit finalization tasks");
        return Collections.emptyList();
    }

    private boolean shouldRunUpgradeTasks() {
        return this.zduManager.getUpgradeStatus().getState() == ZduStatus.State.DISABLED;
    }

    @PluginEventListener
    public void onFinalizationRequested(ZduFinalizationRequestEvent event) {
        for (Message msg : this.upgradeInternal()) {
            logger.error("ZDU finalization error: " + msg);
        }
    }
}

