/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.analytics.events.EventDto
 *  com.atlassian.migration.app.analytics.MultPartUploadAnalyticEvent
 *  org.jetbrains.annotations.NotNull
 */
package com.atlassian.migration.agent.service.analytics;

import com.atlassian.cmpt.analytics.events.EventDto;
import com.atlassian.migration.agent.common.Sink;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.app.AppAnalyticsEventService;
import com.atlassian.migration.app.analytics.MultPartUploadAnalyticEvent;
import java.time.Clock;
import org.jetbrains.annotations.NotNull;

public class DefaultAppAnalyticsEventService
implements AppAnalyticsEventService {
    private Sink<EventDto> analyticsEventSink;
    private Clock time;
    private AnalyticsEventBuilder analyticsEventBuilder;

    public DefaultAppAnalyticsEventService(Sink<EventDto> analyticsEventSink, Clock time, AnalyticsEventBuilder analyticsEventBuilder) {
        this.analyticsEventSink = analyticsEventSink;
        this.time = time;
        this.analyticsEventBuilder = analyticsEventBuilder;
    }

    @Override
    public void sendEvent(@NotNull MultPartUploadAnalyticEvent multPartUploadAnalyticEvent) {
        EventDto eventDto = this.analyticsEventBuilder.buildAppPartUploadEvent(multPartUploadAnalyticEvent, this.time.millis());
        this.analyticsEventSink.put(eventDto);
    }
}

