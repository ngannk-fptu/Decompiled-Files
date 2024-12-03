/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.api.license.event;

public abstract class PluginLicenseEvent {
    private final String pluginKey;

    public PluginLicenseEvent(String pluginKey) {
        this.pluginKey = pluginKey;
    }

    public String getPluginKey() {
        return this.pluginKey;
    }
}

