/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sal.api.pluginsettings;

import com.atlassian.sal.api.pluginsettings.PluginSettings;

public interface PluginSettingsFactory {
    public PluginSettings createSettingsForKey(String var1);

    public PluginSettings createGlobalSettings();
}

