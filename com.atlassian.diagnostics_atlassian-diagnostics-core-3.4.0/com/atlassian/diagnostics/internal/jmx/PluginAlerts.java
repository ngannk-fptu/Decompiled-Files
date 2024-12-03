/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.diagnostics.internal.jmx;

import com.atlassian.diagnostics.internal.jmx.Alerts;
import com.atlassian.diagnostics.internal.jmx.PluginAlertsMXBean;

public class PluginAlerts
extends Alerts
implements PluginAlertsMXBean {
    private final String name;

    public PluginAlerts(String name) {
        this.name = name;
    }

    @Override
    public String getPluginName() {
        return this.name;
    }
}

