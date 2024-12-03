/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.audit.analytics;

public abstract class BaseAnalyticEvent {
    private final String pluginVersion;

    public BaseAnalyticEvent(String pluginVersion) {
        this.pluginVersion = pluginVersion;
    }

    public String getPluginVersion() {
        return this.pluginVersion;
    }
}

