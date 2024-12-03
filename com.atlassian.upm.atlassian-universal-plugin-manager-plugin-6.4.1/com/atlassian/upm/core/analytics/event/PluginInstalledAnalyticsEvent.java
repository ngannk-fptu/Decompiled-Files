/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 */
package com.atlassian.upm.core.analytics.event;

import com.atlassian.plugin.Plugin;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.DefaultHostApplicationInformation;
import com.atlassian.upm.core.Plugins;
import com.atlassian.upm.core.analytics.event.PluginAnalyticsEvent;

public class PluginInstalledAnalyticsEvent
extends PluginAnalyticsEvent {
    public PluginInstalledAnalyticsEvent(Plugin plugin, DefaultHostApplicationInformation hostApplicationInformation, Option<String> sen) {
        super(plugin.getKey(), plugin.getPluginInformation().getVersion(), Plugins.getPluginHostingType(plugin.getPluginInformation(), hostApplicationInformation), sen);
    }

    @Override
    public String getEventType() {
        return "installed";
    }
}

