/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core.analytics.event;

import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.api.util.Pair;
import com.atlassian.upm.core.DefaultHostApplicationInformation;
import com.atlassian.upm.core.HostingType;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.Plugins;
import com.atlassian.upm.core.analytics.AnalyticsEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

public abstract class PluginAnalyticsEvent
implements AnalyticsEvent {
    private final String pluginKey;
    private final String pluginVersion;
    private final boolean connect;
    private final HostingType pluginHosting;
    private final Option<String> sen;

    protected PluginAnalyticsEvent(String pluginKey, String pluginVersion, HostingType pluginHosting, Option<String> sen) {
        this.pluginKey = Objects.requireNonNull(pluginKey, "pluginKey");
        this.pluginVersion = Objects.requireNonNull(pluginVersion, "pluginVersion");
        this.pluginHosting = pluginHosting;
        this.connect = false;
        this.sen = sen;
    }

    protected PluginAnalyticsEvent(Plugin plugin, DefaultHostApplicationInformation hostApplicationInformation, Option<String> sen) {
        this(Objects.requireNonNull(plugin).getKey(), plugin.getVersion(), Plugins.getPluginHostingType(plugin.getPluginInformation(), hostApplicationInformation), sen);
    }

    @Override
    public boolean isRecordedByMarketplace() {
        return true;
    }

    public String getPluginKey() {
        return this.pluginKey;
    }

    public String getPluginVersion() {
        return this.pluginVersion;
    }

    public boolean isConnect() {
        return this.connect;
    }

    public HostingType getPluginHosting() {
        return this.pluginHosting;
    }

    public Option<String> getSen() {
        return this.sen;
    }

    @Override
    public Iterable<AnalyticsEvent.AnalyticsEventInfo> getInvolvedPluginInfo() {
        return Collections.singleton(new AnalyticsEvent.AnalyticsEventInfo(this.getPluginKey(), this.getPluginVersion(), this.getSen()));
    }

    @Override
    public Iterable<Pair<String, String>> getInvolvedPluginVersions() {
        return AnalyticsEvent.AnalyticsEventInfo.getInvolvedPluginVersions(this.getInvolvedPluginInfo());
    }

    @Override
    public Iterable<Pair<String, String>> getMetadata() {
        return Collections.unmodifiableList(Arrays.asList(Pair.pair("connect", Boolean.toString(this.isConnect())), Pair.pair("pluginHosting", this.pluginHosting.getKey())));
    }
}

