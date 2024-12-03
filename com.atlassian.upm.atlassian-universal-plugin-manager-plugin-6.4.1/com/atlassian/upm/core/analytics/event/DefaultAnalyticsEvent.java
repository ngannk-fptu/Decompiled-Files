/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.upm.core.analytics.event;

import com.atlassian.upm.api.util.Pair;
import com.atlassian.upm.core.analytics.AnalyticsEvent;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DefaultAnalyticsEvent
implements AnalyticsEvent {
    private final String type;
    private final Map<String, String> metadata;

    public DefaultAnalyticsEvent(String type) {
        this(type, (Map<String, String>)ImmutableMap.of());
    }

    public DefaultAnalyticsEvent(String type, Map<String, String> metadata) {
        this.type = type;
        this.metadata = ImmutableMap.copyOf(metadata);
    }

    @Override
    public boolean isRecordedByMarketplace() {
        return false;
    }

    @Override
    public String getEventType() {
        return this.type;
    }

    @Override
    public Iterable<Pair<String, String>> getInvolvedPluginVersions() {
        return Collections.emptyList();
    }

    @Override
    public Iterable<AnalyticsEvent.AnalyticsEventInfo> getInvolvedPluginInfo() {
        return Collections.emptyList();
    }

    public List<Pair<String, String>> getMetadata() {
        return this.metadata.entrySet().stream().map(Pair::fromMapEntry).collect(Collectors.toList());
    }
}

