/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.MonitorConfiguration
 */
package com.atlassian.diagnostics.internal;

import com.atlassian.diagnostics.MonitorConfiguration;

public abstract class AlertProvider<T extends MonitorConfiguration> {
    private final String key;
    protected final T monitorConfiguration;

    protected AlertProvider(String key, T monitorConfiguration) {
        this.key = key;
        this.monitorConfiguration = monitorConfiguration;
    }

    public boolean isEnabled() {
        return this.monitorConfiguration.isEnabled();
    }

    public String getKey() {
        return this.key;
    }
}

