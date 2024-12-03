/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.analytics;

import com.atlassian.migration.agent.entity.AnalyticsEventType;
import java.util.Map;

class ExternalAnalyticsEventDto {
    private final AnalyticsEventType eventType;
    private final Map<String, Object> event;

    ExternalAnalyticsEventDto(AnalyticsEventType eventType, Map<String, Object> event) {
        this.eventType = eventType;
        this.event = event;
    }

    public AnalyticsEventType getEventType() {
        return this.eventType;
    }

    public Map<String, Object> getEvent() {
        return this.event;
    }
}

