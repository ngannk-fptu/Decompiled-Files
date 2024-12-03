/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core.analytics.event;

import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.DefaultHostApplicationInformation;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.Plugins;
import com.atlassian.upm.core.analytics.event.PluginAnalyticsEvent;

public class PluginUninstalledAnalyticsEvent
extends PluginAnalyticsEvent {
    public PluginUninstalledAnalyticsEvent(Plugin plugin, DefaultHostApplicationInformation hostApplicationInformation, Option<String> sen) {
        super(plugin.getKey(), plugin.getVersion(), Plugins.getPluginHostingType(plugin.getPluginInformation(), hostApplicationInformation), sen);
    }

    @Override
    public String getEventType() {
        return "uninstalled";
    }
}

