/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.analytics;

import com.atlassian.migration.agent.entity.AnalyticsEvent;
import java.util.List;

public class ProcessedAnalyticsEvents {
    private final List<AnalyticsEvent> successfullySentEvents;
    private final List<AnalyticsEvent> unsuccessfullySentEvents;

    public ProcessedAnalyticsEvents(List<AnalyticsEvent> successfullySentEvents, List<AnalyticsEvent> unsuccessfullySentEvents) {
        this.successfullySentEvents = successfullySentEvents;
        this.unsuccessfullySentEvents = unsuccessfullySentEvents;
    }

    public List<AnalyticsEvent> getSuccessfullySentEvents() {
        return this.successfullySentEvents;
    }

    public List<AnalyticsEvent> getUnsuccessfullySentEvents() {
        return this.unsuccessfullySentEvents;
    }
}

