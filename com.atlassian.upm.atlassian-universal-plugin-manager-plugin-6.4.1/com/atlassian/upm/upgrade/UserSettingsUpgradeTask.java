/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.upgrade.PluginUpgradeTask
 *  com.atlassian.sal.api.user.UserKey
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.upgrade;

import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.upgrade.PluginUpgradeTask;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.upm.UpmInformation;
import com.atlassian.upm.UserSettings;
import com.atlassian.upm.UserSettingsStore;
import com.atlassian.upm.core.impl.NamespacedPluginSettings;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserSettingsUpgradeTask
implements PluginUpgradeTask {
    private static final Logger log = LoggerFactory.getLogger(UserSettingsUpgradeTask.class);
    private static final String OLD_EMAIL_SETTINGS_KEY_PREFIX = "com.atlassian.upm.mail.impl.PluginSettingsUserEmailSettingsStore:user-email-settings:";
    private static final String OLD_EMAIL_SETTINGS_USERS_KEY = "user-email-settings";
    private final PluginSettingsFactory pluginSettingsFactory;
    private final UpmInformation upm;
    private final UserSettingsStore userSettingsStore;

    public UserSettingsUpgradeTask(PluginSettingsFactory pluginSettingsFactory, UpmInformation upm, UserSettingsStore userSettingsStore) {
        this.pluginSettingsFactory = Objects.requireNonNull(pluginSettingsFactory, "pluginSettingsFactory");
        this.upm = Objects.requireNonNull(upm, "upm");
        this.userSettingsStore = Objects.requireNonNull(userSettingsStore, "userSettingsStore");
    }

    public int getBuildNumber() {
        return 4;
    }

    public String getShortDescription() {
        return "Upgrades user-related data storage to support additional per-user UPM settings";
    }

    public Collection<Message> doUpgrade() throws Exception {
        log.info("Running UPM UserSettings upgrade task");
        this.doUserSettingsUpgrade();
        return null;
    }

    public String getPluginKey() {
        return this.upm.getPluginKey();
    }

    private void doUserSettingsUpgrade() {
        NamespacedPluginSettings oldSettings = new NamespacedPluginSettings(this.pluginSettingsFactory.createGlobalSettings(), OLD_EMAIL_SETTINGS_KEY_PREFIX);
        Object entries = oldSettings.get(OLD_EMAIL_SETTINGS_USERS_KEY);
        if (entries instanceof List) {
            List userKeys = (List)entries;
            for (String userKeyStr : userKeys) {
                this.userSettingsStore.setBoolean(new UserKey(userKeyStr), UserSettings.DISABLE_EMAIL, true);
            }
        }
        if (entries != null) {
            oldSettings.remove(OLD_EMAIL_SETTINGS_USERS_KEY);
        }
    }
}

