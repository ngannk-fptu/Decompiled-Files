/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.sal.api.upgrade.PluginUpgradeTask
 *  com.atlassian.sal.core.upgrade.DefaultPluginUpgradeManager
 *  com.google.common.base.Stopwatch
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.sal.confluence.lifecycle;

import com.atlassian.beehive.ClusterLockService;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.upgrade.PluginUpgradeTask;
import com.atlassian.sal.core.upgrade.DefaultPluginUpgradeManager;
import com.google.common.base.Stopwatch;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MeasuringPluginUpgradeManager
extends DefaultPluginUpgradeManager {
    private static Logger log = LoggerFactory.getLogger(MeasuringPluginUpgradeManager.class);

    public MeasuringPluginUpgradeManager(List<PluginUpgradeTask> upgradeTasks, TransactionTemplate transactionTemplate, PluginAccessor pluginAccessor, PluginSettingsFactory pluginSettingsFactory, PluginEventManager pluginEventManager, ClusterLockService clusterLockService) {
        super(upgradeTasks, transactionTemplate, pluginAccessor, pluginSettingsFactory, pluginEventManager, clusterLockService);
    }

    public List<Message> upgradeInternal(Plugin plugin) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        List messages = super.upgradeInternal(plugin);
        log.debug("Upgrading {} took {}", (Object)plugin, (Object)stopwatch);
        return messages;
    }
}

