/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.upgrade.PluginUpgradeTask
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.sal.core.upgrade;

import com.atlassian.plugin.Plugin;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.upgrade.PluginUpgradeTask;
import com.atlassian.sal.core.message.DefaultMessage;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginUpgrader {
    public static final String BUILD = ":build";
    protected List<PluginUpgradeTask> upgradeTasks;
    private final String buildSettingsKey;
    private static final Logger log = LoggerFactory.getLogger(PluginUpgrader.class);
    protected Plugin plugin;
    protected PluginSettings pluginSettings;
    protected List<Message> errors = new ArrayList<Message>();
    private static final Comparator<PluginUpgradeTask> UPGRADE_TASK_COMPARATOR = (t1, t2) -> {
        if (t1 == null) {
            return -1;
        }
        if (t2 == null) {
            return 1;
        }
        if (t1.getBuildNumber() > t2.getBuildNumber()) {
            return 1;
        }
        return t1.getBuildNumber() < t2.getBuildNumber() ? -1 : 0;
    };

    protected PluginUpgrader(Plugin plugin, PluginSettings pluginSettings, List<PluginUpgradeTask> upgradeTasks) {
        this(plugin, pluginSettings, BUILD, upgradeTasks);
    }

    protected PluginUpgrader(Plugin plugin, PluginSettings pluginSettings, String buildSettingsKey, List<PluginUpgradeTask> upgradeTasks) {
        this.plugin = plugin;
        this.pluginSettings = pluginSettings;
        this.upgradeTasks = new ArrayList<PluginUpgradeTask>(upgradeTasks);
        this.buildSettingsKey = buildSettingsKey;
        Collections.sort(this.upgradeTasks, UPGRADE_TASK_COMPARATOR);
    }

    protected List<Message> upgrade() {
        if (this.needUpgrade()) {
            this.doUpgrade();
        }
        return this.errors;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void doUpgrade() {
        try {
            log.info("Upgrading plugin {}", (Object)this.plugin.getKey());
            for (PluginUpgradeTask upgradeTask : this.upgradeTasks) {
                if (upgradeTask.getBuildNumber() <= this.getDataBuildNumber()) continue;
                Collection messages = upgradeTask.doUpgrade();
                if (messages == null || messages.isEmpty()) {
                    this.upgradeTaskSucceeded(upgradeTask);
                    continue;
                }
                this.upgradeTaskFailed(upgradeTask, messages);
            }
        }
        catch (Exception | LinkageError e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            this.errors.add(new DefaultMessage("Unexpected exception caught during plugin upgrade: " + sw.toString(), new Serializable[0]));
            log.error("Upgrade failed:", e);
        }
        finally {
            this.postUpgrade();
        }
    }

    protected void upgradeTaskSucceeded(PluginUpgradeTask upgradeTask) {
        this.setDataBuildNumber(upgradeTask.getBuildNumber());
        log.info("Upgraded plugin {} to version {} - {}", new Object[]{upgradeTask.getPluginKey(), upgradeTask.getBuildNumber(), upgradeTask.getShortDescription()});
    }

    protected void upgradeTaskFailed(PluginUpgradeTask upgradeTask, Collection<Message> messages) {
        this.errors.addAll(messages);
        StringBuilder msg = new StringBuilder();
        msg.append("Plugin upgrade failed for ").append(upgradeTask.getPluginKey());
        msg.append(" to version ").append(upgradeTask.getBuildNumber());
        msg.append(" - ").append(upgradeTask.getShortDescription());
        msg.append("\n");
        for (Message message : messages) {
            msg.append("\t* ").append(message.getKey()).append(" ").append(Arrays.toString(message.getArguments()));
        }
        log.warn("upgradeTaskFailed: {}", (Object)msg.toString());
    }

    protected List<Message> getErrors() {
        return this.errors;
    }

    protected boolean needUpgrade() {
        int dataBuildNumber = 0;
        int lastUpgradeTaskBuildNumber = 0;
        try {
            PluginUpgradeTask lastUpgradeTask = this.upgradeTasks.get(this.upgradeTasks.size() - 1);
            dataBuildNumber = this.getDataBuildNumber();
            if (null != lastUpgradeTask && lastUpgradeTask.getBuildNumber() != 0) {
                lastUpgradeTaskBuildNumber = lastUpgradeTask.getBuildNumber();
                log.debug("Plugin: {}, current version: {}, highest upgrade task found: {}.", new Object[]{this.plugin.getKey(), dataBuildNumber, lastUpgradeTaskBuildNumber});
            }
        }
        catch (Exception | LinkageError e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            this.errors.add(new DefaultMessage("Unexpected exception caught during plugin upgrade check: " + sw.toString(), new Serializable[0]));
            log.error("Unexpected exception caught during plugin upgrade check:", e);
        }
        return lastUpgradeTaskBuildNumber > dataBuildNumber;
    }

    protected int getDataBuildNumber() {
        String val = (String)this.pluginSettings.get(this.plugin.getKey() + this.buildSettingsKey);
        if (val != null) {
            return Integer.parseInt(val);
        }
        return 0;
    }

    protected void setDataBuildNumber(int buildNumber) {
        this.pluginSettings.put(this.plugin.getKey() + this.buildSettingsKey, (Object)String.valueOf(buildNumber));
    }

    protected void postUpgrade() {
        log.info("Plugin {}  upgrade completed. Current version is: {}", (Object)this.plugin.getKey(), (Object)this.getDataBuildNumber());
    }
}

