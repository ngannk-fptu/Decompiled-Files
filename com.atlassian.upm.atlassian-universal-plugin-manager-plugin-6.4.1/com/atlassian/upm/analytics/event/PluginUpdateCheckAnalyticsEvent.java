/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.analytics.event;

import com.atlassian.upm.analytics.event.UpmAnalyticsEvent;
import com.atlassian.upm.api.util.Pair;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.analytics.AnalyticsEvent;
import com.atlassian.upm.core.analytics.SenFinder;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class PluginUpdateCheckAnalyticsEvent
extends UpmAnalyticsEvent {
    private final Iterable<AnalyticsEvent.AnalyticsEventInfo> pluginInfo;
    private final boolean userInitiated;

    public PluginUpdateCheckAnalyticsEvent(Iterable<Plugin> plugins, boolean userInitiated, SenFinder senFinder) {
        this.pluginInfo = StreamSupport.stream(plugins.spliterator(), false).map(p -> new AnalyticsEvent.AnalyticsEventInfo(p.getKey(), p.getPluginInformation().getVersion(), senFinder.findSen((Plugin)p))).collect(Collectors.toList());
        this.userInitiated = userInitiated;
    }

    public boolean isUserInitiated() {
        return this.userInitiated;
    }

    @Override
    public String getEventType() {
        return "updates";
    }

    @Override
    public Iterable<AnalyticsEvent.AnalyticsEventInfo> getInvolvedPluginInfo() {
        return this.pluginInfo;
    }

    @Override
    public Iterable<Pair<String, String>> getMetadata() {
        return Collections.singletonList(Pair.pair("automated", Boolean.toString(!this.isUserInitiated())));
    }
}

