/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.analytics.event;

import com.atlassian.upm.analytics.event.UpmAnalyticsEvent;
import com.atlassian.upm.api.util.Pair;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

public class PluginRequestedAnalyticsEvent
extends UpmAnalyticsEvent {
    private final String pluginKey;
    private final boolean newRequest;

    public PluginRequestedAnalyticsEvent(String pluginKey, boolean newRequest) {
        this.pluginKey = Objects.requireNonNull(pluginKey, "pluginKey");
        this.newRequest = newRequest;
    }

    public String getPluginKey() {
        return this.pluginKey;
    }

    public boolean isNewRequest() {
        return this.newRequest;
    }

    @Override
    public String getEventType() {
        return "requested";
    }

    @Override
    public Iterable<Pair<String, String>> getInvolvedPluginVersions() {
        return Collections.emptyList();
    }

    @Override
    public Iterable<Pair<String, String>> getMetadata() {
        return Collections.unmodifiableList(Arrays.asList(Pair.pair("new-request", Boolean.toString(this.isNewRequest())), Pair.pair("pk", this.pluginKey)));
    }
}

