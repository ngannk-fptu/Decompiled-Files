/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.analytics.event;

import com.atlassian.upm.core.analytics.event.DefaultAnalyticsEvent;
import java.util.Map;

public class UpmUiAnalyticsEvent
extends DefaultAnalyticsEvent {
    public UpmUiAnalyticsEvent(String type) {
        super(type);
    }

    public UpmUiAnalyticsEvent(String type, Map<String, String> metadata) {
        super(type, metadata);
    }

    @Override
    public boolean isRecordedByMarketplace() {
        return true;
    }
}

