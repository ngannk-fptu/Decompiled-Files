/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 */
package com.atlassian.plugins.authentication.upgrade;

import com.atlassian.sal.api.pluginsettings.PluginSettings;

public class UpgradeUtils {
    private UpgradeUtils() {
    }

    public static void rename(PluginSettings globalSettings, String from, String to) {
        Object value = globalSettings.get(from);
        if (value != null) {
            globalSettings.put(to, value);
            globalSettings.remove(from);
        }
    }
}

