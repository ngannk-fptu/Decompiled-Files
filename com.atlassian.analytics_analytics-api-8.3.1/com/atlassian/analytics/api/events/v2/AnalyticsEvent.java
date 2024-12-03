/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.analytics.api.events.v2;

import java.util.Optional;

@Deprecated
public interface AnalyticsEvent {
    default public Optional<String> getEventName() {
        return Optional.empty();
    }
}

