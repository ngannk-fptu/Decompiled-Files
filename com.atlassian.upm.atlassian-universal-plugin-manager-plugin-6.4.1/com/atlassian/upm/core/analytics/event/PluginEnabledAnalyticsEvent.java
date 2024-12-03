/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core.analytics.event;

import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.DefaultHostApplicationInformation;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.analytics.event.PluginAnalyticsEvent;

public class PluginEnabledAnalyticsEvent
extends PluginAnalyticsEvent {
    public PluginEnabledAnalyticsEvent(Plugin plugin, DefaultHostApplicationInformation hostApplicationInformation, Option<String> sen) {
        super(plugin, hostApplicationInformation, sen);
    }

    @Override
    public String getEventType() {
        return "enabled";
    }
}

