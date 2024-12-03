/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 */
package com.atlassian.sal.core.pluginsettings;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.core.util.Assert;

public class PrefixedPluginSettingsDelegate
implements PluginSettings {
    private final String prefix;
    private final PluginSettings target;

    public PrefixedPluginSettingsDelegate(String prefix, PluginSettings target) {
        Assert.notNull(prefix, "Prefix must not be null");
        Assert.notNull(target, "Target must not be null");
        this.prefix = prefix;
        this.target = target;
    }

    public Object get(String key) {
        return this.target.get(this.prefix + key);
    }

    public Object put(String key, Object value) {
        return this.target.put(this.prefix + key, value);
    }

    public Object remove(String key) {
        return this.target.remove(this.prefix + key);
    }
}

