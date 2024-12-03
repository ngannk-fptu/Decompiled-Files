/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 */
package com.atlassian.upm.core.impl;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.upm.impl.LongKeyHasher;

public class NamespacedPluginSettings
implements PluginSettings {
    private PluginSettings pluginSettings;
    private String keyPrefix;

    public NamespacedPluginSettings(PluginSettings pluginSettings, String keyPrefix) {
        this.pluginSettings = pluginSettings;
        this.keyPrefix = keyPrefix;
    }

    public Object get(String s) {
        return this.pluginSettings.get(LongKeyHasher.hashKeyIfTooLong(this.keyPrefix + s));
    }

    public Object put(String s, Object o) {
        return this.pluginSettings.put(LongKeyHasher.hashKeyIfTooLong(this.keyPrefix + s), o);
    }

    public Object remove(String s) {
        return this.pluginSettings.remove(LongKeyHasher.hashKeyIfTooLong(this.keyPrefix + s));
    }
}

