/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.analytics.event;

import com.atlassian.upm.analytics.event.UpmAnalyticsEvent;
import com.atlassian.upm.api.util.Pair;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

public class PluginRequestCompletedAnalyticsEvent
extends UpmAnalyticsEvent {
    private final String pluginKey;
    private final boolean fulfilled;
    private final int count;

    public PluginRequestCompletedAnalyticsEvent(String pluginKey, boolean fulfilled, int count) {
        this.pluginKey = Objects.requireNonNull(pluginKey, "pluginKey");
        this.fulfilled = fulfilled;
        this.count = count;
    }

    public String getPluginKey() {
        return this.pluginKey;
    }

    public boolean isFulfilled() {
        return this.fulfilled;
    }

    public int getCount() {
        return this.count;
    }

    @Override
    public String getEventType() {
        return this.isFulfilled() ? "request-fulfilled" : "request-dismissed";
    }

    @Override
    public Iterable<Pair<String, String>> getInvolvedPluginVersions() {
        return Collections.emptyList();
    }

    @Override
    public Iterable<Pair<String, String>> getMetadata() {
        return Collections.unmodifiableList(Arrays.asList(Pair.pair("pk", this.getPluginKey()), Pair.pair("fulfilled", Boolean.toString(this.isFulfilled())), Pair.pair("count", Integer.toString(this.count))));
    }
}

