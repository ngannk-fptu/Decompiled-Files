/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.upgrade.PluginUpgradeTask
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.custom_apps.upgrade;

import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.upgrade.PluginUpgradeTask;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitialiseCustomOrderFlagUpgradeTask
implements PluginUpgradeTask {
    private static final Logger log = LoggerFactory.getLogger(InitialiseCustomOrderFlagUpgradeTask.class);
    private static final String CUSTOM_APPS_AS_JSON = "com.atlassian.plugins.custom_apps.customAppsAsJSON";
    private static final String HAS_CUSTOM_ORDER = "com.atlassian.plugins.custom_apps.hasCustomOrder";
    private final PluginSettingsFactory pluginSettingsFactory;

    public InitialiseCustomOrderFlagUpgradeTask(PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    public int getBuildNumber() {
        return 1;
    }

    public String getShortDescription() {
        return "Initialise the value of com.atlassian.plugins.custom_apps.hasCustomOrder";
    }

    public Collection<Message> doUpgrade() throws Exception {
        log.info("Starting plugin upgrade");
        PluginSettings globalSettings = this.pluginSettingsFactory.createGlobalSettings();
        Object hasCustomOrder = globalSettings.get(HAS_CUSTOM_ORDER);
        if (hasCustomOrder == null) {
            if (globalSettings.get(CUSTOM_APPS_AS_JSON) == null) {
                log.info("Initialising flag to false");
                globalSettings.put(HAS_CUSTOM_ORDER, (Object)"false");
            } else {
                log.info("Initialising flag to true");
                globalSettings.put(HAS_CUSTOM_ORDER, (Object)"true");
            }
        }
        return null;
    }

    public String getPluginKey() {
        return "com.atlassian.plugins.atlassian-nav-links-plugin";
    }
}

