/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.analytics.event;

import com.atlassian.upm.api.util.Pair;
import com.atlassian.upm.core.analytics.AnalyticsEvent;
import java.util.Collections;

public abstract class UpmAnalyticsEvent
implements AnalyticsEvent {
    @Override
    public boolean isRecordedByMarketplace() {
        return true;
    }

    @Override
    public Iterable<AnalyticsEvent.AnalyticsEventInfo> getInvolvedPluginInfo() {
        return Collections.emptyList();
    }

    @Override
    public Iterable<Pair<String, String>> getInvolvedPluginVersions() {
        return AnalyticsEvent.AnalyticsEventInfo.getInvolvedPluginVersions(this.getInvolvedPluginInfo());
    }
}

